package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.dictionaryhasresearchgroup.DictionaryHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryBean;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.view.dictionary.beans.DictionaryEntriesBean;

@Service
public class DictionaryService {

    private final DictionaryDao dictionaryDao;
    private final DictionaryEntryDao dictionaryEntryDao;

    private final DictionaryHasResearchGroupDao dictionaryHasResearchGroupDao;

    @Autowired
    public DictionaryService(DictionaryDao dictionaryDao, DictionaryEntryDao dictionaryEntryDao, DictionaryHasResearchGroupDao dictionaryHasResearchGroupDao) {
        this.dictionaryDao = dictionaryDao;
        this.dictionaryEntryDao = dictionaryEntryDao;
        this.dictionaryHasResearchGroupDao = dictionaryHasResearchGroupDao;
    }

    public List<DictionaryHasResearchGroup> listDictionaryHasResearchGroupByDictionaryId(Long dictionaryId) {
        return this.dictionaryHasResearchGroupDao.listByDictionaryId(dictionaryId);
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


    private void createOrUpdateUserGroups(DictionaryBean dictionaryBean) {
        if (dictionaryBean.getResearchGroupRelationshipBean() == null && dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<DictionaryHasResearchGroup> toCreate = new ArrayList<>();
        List<DictionaryHasResearchGroup> toDelete = new ArrayList<>();
        List<DictionaryHasResearchGroup> currentlySelected = listDictionaryHasResearchGroupByDictionaryId(dictionaryBean.getId());

        for (DictionaryHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }


        for (String researchGroupId : dictionaryBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (DictionaryHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                DictionaryHasResearchGroup rghau = new DictionaryHasResearchGroup();
                rghau.setDictionaryId(dictionaryBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (DictionaryHasResearchGroup toCre : toCreate) {
            dictionaryHasResearchGroupDao.create(toCre);
        }
        for (DictionaryHasResearchGroup toDel : toDelete) {
            dictionaryHasResearchGroupDao.delete(toDel);
        }

    }

    public Dictionary createOrUpdate(DictionaryBean dictionaryBean) {

        Dictionary dictionary = new Dictionary();
        dictionary.setId(dictionaryBean.getId());
        dictionary.setDictionaryName(dictionaryBean.getDictionaryName());
        dictionary.setHasGroundTruth(dictionaryBean.getHasGroundTruth() == null ? false : dictionaryBean.getHasGroundTruth());

        if (dictionary.getId() == null) {
            dictionary = dictionaryDao.create(dictionary);
            dictionaryBean.setId(dictionary.getId());
            createOrUpdateUserGroups(dictionaryBean);
        } else {
            dictionaryDao.update(dictionary);
            createOrUpdateUserGroups(dictionaryBean);
        }
        return dictionary;

    }
}
