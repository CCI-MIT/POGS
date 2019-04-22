package edu.mit.cci.pogs.view.dictionary.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;

public class DictionaryBean extends Dictionary {


    private List<UnprocessedDictionaryEntry> unprocessedDictionaryEntries;


    public DictionaryBean(Dictionary dict) {
        super(dict);
    }

    public List<UnprocessedDictionaryEntry> getUnprocessedDictionaryEntries() {
        return unprocessedDictionaryEntries;
    }

    public void setUnprocessedDictionaryEntries(List<UnprocessedDictionaryEntry> unprocessedDictionaryEntries) {
        this.unprocessedDictionaryEntries = unprocessedDictionaryEntries;
    }

    public Integer getTotalOfUnprocessedDictionaryEntries() {
        if (unprocessedDictionaryEntries == null) {
            return 0;
        } else {
            return unprocessedDictionaryEntries.size();
        }
    }
}
