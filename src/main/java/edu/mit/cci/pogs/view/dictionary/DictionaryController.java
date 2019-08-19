package edu.mit.cci.pogs.view.dictionary;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryType;
import edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.UnprocessedDictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.service.DictionaryService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryBean;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntriesBean;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntryWorkspaceBean;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryWorkspaceBean;

@Controller
public class DictionaryController {

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

    @GetMapping("/admin/dictionaries")
    public String getDictionaries(Model model) {

        List<DictionaryBean> dictionaryBeanList = new ArrayList<>();
        for(Dictionary d: dictionaryDao.list()) {
            DictionaryBean db = new DictionaryBean(d);
            db.setUnprocessedDictionaryEntries(unprocessedDictionaryEntryDao.listNotProcessedDictionaryEntriesByDictionary(d.getId()));
            dictionaryBeanList.add(db);
        }

        model.addAttribute("dictionaryList", dictionaryBeanList);
        return "dictionary/dictionaries-list";
    }





    @GetMapping("/dictionaries/{dictionaryId}/image")
    public void getDictionaryImage(@PathVariable("dictionaryId") Long dictionaryId,
                                   HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(value = "backgroundColor", required = false) String backgroundColor ) throws IOException{

        if(backgroundColor == null){
            backgroundColor = "#ffffff";
        }
        File file = this.dictionaryService.generateImageFromDictionary(request, dictionaryId, backgroundColor);
        final ServletContext servletContext = request.getServletContext();
        final String mimeType = servletContext.getMimeType(file.getAbsolutePath());
        if (mimeType == null) {
            throw new IOException("Cannot resolve mime type for file " + file.getAbsolutePath());
        }
        response.setContentType(mimeType);

        try (FileInputStream in = new FileInputStream(file)) {
            final int count = IOUtils.copy(in, response.getOutputStream());
            response.setContentLength(count);
        }

    }


    @GetMapping("/admin/dictionaries/{dictionaryId}")
    public String getDictionary(@PathVariable("dictionaryId") Long dictionaryId, Model model) {
        Dictionary dictionary = dictionaryDao.get(dictionaryId);
        model.addAttribute("dictionary", dictionary);
        DictionaryEntriesBean dictionaryEntriesBean = new DictionaryEntriesBean();
        dictionaryEntriesBean.setDictionaryId(dictionary.getId());
        dictionaryEntriesBean.setDictionaryEntryList(dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryId));

        model.addAttribute("dictionaryEntriesBean", dictionaryEntriesBean);
        return "dictionary/dictionary-display";
    }

    @GetMapping("admin/dictionaries/create")
    public String createDictionary(Model model) {

        Dictionary dictionary = new Dictionary();
        model.addAttribute("dictionary",dictionary);
        return "dictionary/dictionary-edit";
    }
    @GetMapping("/admin/dictionaries/{dictionaryId}/edit")
    public String editDictionary(@PathVariable("dictionaryId") Long dictionaryId, Model model) {
        Dictionary dictionary = dictionaryDao.get(dictionaryId);
        model.addAttribute("dictionary", dictionary);
        return "dictionary/dictionary-edit";
    }
    @PostMapping("/admin/dictionaries")
    public String saveChatScript(@ModelAttribute Dictionary dictionary, RedirectAttributes redirectAttributes) {

        if (dictionary.getId() == null) {
            dictionary = dictionaryDao.create(dictionary);
            MessageUtils.addSuccessMessage("Dictionary created successfully!", redirectAttributes);
        } else {
            dictionaryDao.update(dictionary);
            MessageUtils.addSuccessMessage("Dictionary updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/dictionaries/"+dictionary.getId();
    }

    @GetMapping("/admin/dictionaries/{dictionaryId}/dictionaryentries/edit")
    public String editChatEntries(@PathVariable("dictionaryId") Long dictionaryId, Model model) {

        Dictionary dictionary = dictionaryDao.get(dictionaryId);
        model.addAttribute("dictionary", dictionary);

        DictionaryEntriesBean dictionaryEntriesBean = new DictionaryEntriesBean();
        dictionaryEntriesBean.setDictionaryId(dictionary.getId());
        dictionaryEntriesBean.setDictionaryEntryList(dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryId));
        model.addAttribute("dictionaryEntriesBean", dictionaryEntriesBean);

        return "dictionary/dictionaryentry-edit";
    }
    @PostMapping("/admin/dictionaries/dictionaryentries/edit")
    public String saveDictionaryEntries(@ModelAttribute DictionaryEntriesBean dictionaryEntriesBean, RedirectAttributes redirectAttributes) {

        dictionaryService.updateDictionaryEntryList(dictionaryEntriesBean);
        return "redirect:/admin/dictionaries/" + dictionaryEntriesBean.getDictionaryId();
    }
}
