package edu.mit.cci.pogs.view.dictionary.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;

public class UnprocessedDictionaryEntriesBean {

    private List<UnprocessedDictionaryEntry> dictionaryEntryList;

    private Long dictionaryId;

    public List<UnprocessedDictionaryEntry> getDictionaryEntryList() {
        return dictionaryEntryList;
    }

    public void setDictionaryEntryList(List<UnprocessedDictionaryEntry> dictionaryEntryList) {
        this.dictionaryEntryList = dictionaryEntryList;
    }

    public Long getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}
