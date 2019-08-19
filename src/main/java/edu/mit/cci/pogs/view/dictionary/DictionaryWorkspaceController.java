package edu.mit.cci.pogs.view.dictionary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.UnprocessedDictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.service.DictionaryService;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntryWorkspaceBean;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryWorkspaceBean;

@RestController
public class DictionaryWorkspaceController {

    @Autowired
    private DictionaryEntryDao dictionaryEntryDao;

    @Autowired
    private DictionaryDao dictionaryDao;


    @GetMapping("/dictionaries/{dictionaryId}")
    public DictionaryWorkspaceBean getDictionaryJson(@PathVariable("dictionaryId") Long dictionaryId) {

        Dictionary d = dictionaryDao.get(dictionaryId);
        DictionaryWorkspaceBean dwb = new DictionaryWorkspaceBean();
        dwb.setDictionaryName(d.getDictionaryName());
        dwb.setHasGroundTruth(d.getHasGroundTruth());
        List<DictionaryEntry> allEntries = dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryId);
        dwb.setDictionaryEntries(new ArrayList<>());
        for(DictionaryEntry de: allEntries){
            dwb.getDictionaryEntries().add(de.getId());
        }
        return dwb;


    }
    @GetMapping("/dictionaries/{dictionaryId}/dictionaryentries/{dictionaryEntryId}")
    public DictionaryEntryWorkspaceBean getDictionaryEntryJson(@PathVariable("dictionaryId") Long dictionaryId,
                                                               @PathVariable("dictionaryEntryId") Long dictionaryEntryId) {

        DictionaryEntryWorkspaceBean dewb = new DictionaryEntryWorkspaceBean();
        DictionaryEntry de = dictionaryEntryDao.get(dictionaryEntryId);
        if(de == null ) return null;

        dewb.setDictionaryId(de.getDictionaryId());
        dewb.setEntryValue(Base64.getEncoder().encodeToString(de.getEntryValue().getBytes()));
        dewb.setEntryCategory(de.getEntryCategory());
        dewb.setEntryType(de.getEntryType());
        dewb.setId(de.getId());

        return dewb;

    }
}
