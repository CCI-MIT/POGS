package edu.mit.cci.pogs.view.dictionary.beans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;

public class DictionaryWorkspaceBean extends Dictionary {

    public List<Long> getDictionaryEntries() {
        return dictionaryEntries;
    }

    public void setDictionaryEntries(List<Long> dictionaryEntries) {
        this.dictionaryEntries = dictionaryEntries;
    }

    private List<Long>  dictionaryEntries;
}
