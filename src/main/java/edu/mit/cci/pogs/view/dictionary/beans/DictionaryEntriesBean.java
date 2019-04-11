package edu.mit.cci.pogs.view.dictionary.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;

public class DictionaryEntriesBean {

   private List<DictionaryEntry> dictionaryEntryList;

   private Long dictionaryId;

    public List<DictionaryEntry> getDictionaryEntryList() {
        return dictionaryEntryList;
    }

    public void setDictionaryEntryList(List<DictionaryEntry> dictionaryEntryList) {
        this.dictionaryEntryList = dictionaryEntryList;
    }

    public Long getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}
