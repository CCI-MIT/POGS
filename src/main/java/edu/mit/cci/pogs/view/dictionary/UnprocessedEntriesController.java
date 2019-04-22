package edu.mit.cci.pogs.view.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryType;
import edu.mit.cci.pogs.model.dao.session.TeamCreationType;
import edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.UnprocessedDictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;
import edu.mit.cci.pogs.service.DictionaryService;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryBean;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntriesBean;
import edu.mit.cci.pogs.view.dictionary.beans.UnprocessedDictionaryEntriesBean;
import edu.mit.cci.pogs.view.session.beans.SubjectsBean;

@Controller
public class UnprocessedEntriesController {
    @Autowired
    private DictionaryEntryDao dictionaryEntryDao;

    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private UnprocessedDictionaryEntryDao unprocessedDictionaryEntryDao;

    @ModelAttribute("entryTypes")
    public List<DictionaryEntryType> getDictionaryEntryTypes() {
        return Arrays.asList(DictionaryEntryType.values());
    }

    @GetMapping("/admin/dictionaries/{dictionaryId}/unprocessed")
    public String getDictionary(@PathVariable("dictionaryId") Long dictionaryId, Model model) {
        Dictionary dictionary = dictionaryDao.get(dictionaryId);

        model.addAttribute("dictionary", dictionary);



        List<String> availableCategories = new ArrayList<>();
        for(DictionaryEntry de: dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryId)) {
            if(de.getEntryCategory()!=null && !de.getEntryCategory().isEmpty()) {
                availableCategories.add(de.getEntryCategory());
            }
        }

        List<UnprocessedDictionaryEntry> unprocessed = unprocessedDictionaryEntryDao
                .listNotProcessedDictionaryEntriesByDictionary(dictionary.getId());

        for(UnprocessedDictionaryEntry ude : unprocessed){
            if(ude.getEntryPredictedCategory()!=null && !ude.getEntryPredictedCategory().isEmpty()) {
                availableCategories.add(ude.getEntryPredictedCategory());
            }
        }
        Collections.sort(availableCategories);
        model.addAttribute("availableCategories", availableCategories);
        UnprocessedDictionaryEntriesBean ude = new UnprocessedDictionaryEntriesBean();
        ude.setDictionaryId(dictionary.getId());
        ude.setDictionaryEntryList(unprocessed);
        model.addAttribute("unprocessedDictionaryEntriesBean", ude);



        return "dictionary/dictionary-unprocess";
    }

    @PostMapping("/admin/dictionaries/unprocessed")
    public String saveEntries(@ModelAttribute UnprocessedDictionaryEntriesBean unprocessedDictionaryEntriesBean, Model model) {
        Dictionary dictionary = dictionaryDao.get(unprocessedDictionaryEntriesBean.getDictionaryId());

        for(UnprocessedDictionaryEntry ude : unprocessedDictionaryEntriesBean.getDictionaryEntryList()) {
            for (DictionaryEntry de : dictionaryEntryDao.listDictionaryEntriesByDictionary(unprocessedDictionaryEntriesBean.getDictionaryId())) {
                if(ude.getEntryPredictedCategory().equals(de.getEntryCategory())){
                    String entryValue = de.getEntryValue();
                    if(entryValue.endsWith("\n")) {
                        entryValue +=  ude.getEntryValue() + "\n";
                    } else {
                        entryValue += "\n" +ude.getEntryValue() + "\n";
                    }
                    de.setEntryValue(entryValue);
                    dictionaryEntryDao.update(de);
                    ude.setHasBeenProcessed(true);
                }

            }
            if(!ude.getHasBeenProcessed()) {
                DictionaryEntry de = new DictionaryEntry();
                de.setDictionaryId(unprocessedDictionaryEntriesBean.getDictionaryId());
                de.setEntryType(ude.getEntryType());
                de.setEntryCategory(ude.getEntryPredictedCategory());
                de.setEntryValue(de.getEntryValue() + "\n");
                dictionaryEntryDao.create(de);
                ude.setHasBeenProcessed(true);
            }
            unprocessedDictionaryEntryDao.update(ude);
        }
        return "redirect:/admin/dictionaries";
    }
}
