package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntriesBean;

@Service
public class DictionaryService {

    private final DictionaryDao dictionaryDao;
    private final DictionaryEntryDao dictionaryEntryDao;

    @Autowired
    public DictionaryService(DictionaryDao dictionaryDao, DictionaryEntryDao dictionaryEntryDao) {
        this.dictionaryDao = dictionaryDao;
        this.dictionaryEntryDao = dictionaryEntryDao;
    }

    public void updateDictionaryEntryList(DictionaryEntriesBean dictionaryEntriesBean) {
        List<DictionaryEntry> dictionaryEntryList = dictionaryEntriesBean.getDictionaryEntryList();
        List<DictionaryEntry> existingDictEntries = dictionaryEntryDao.listDictionaryEntriesByDictionary(dictionaryEntriesBean.getDictionaryId());

        for (DictionaryEntry dictEntry : dictionaryEntryList) {
            dictEntry.setDictionaryId(dictionaryEntriesBean.getDictionaryId());
            if(dictEntry.getId()!=null){
                dictionaryEntryDao.update(dictEntry);
                existingDictEntries.remove(dictEntry);
            }else{
                dictionaryEntryDao.create(dictEntry);
            }
        }

        for (DictionaryEntry dictEnt: existingDictEntries){
            dictionaryEntryDao.deleteDictionaryEntry(dictEnt);
        }
    }

    public JSONArray listDictionaryEntriesJson(Long dictionaryId) {

        List<DictionaryEntry> taskExecutionAttributes = dictionaryEntryDao
                .listDictionaryEntriesByDictionary(dictionaryId);
        JSONArray configurationArray = new JSONArray();
        for (DictionaryEntry tea : taskExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("entryType", tea.getEntryType());
            teaJson.put("entryCategory", tea.getEntryCategory());
            teaJson.put("entryValue", tea.getEntryValue());
            configurationArray.put(teaJson);
        }
        return configurationArray;
    }
}
