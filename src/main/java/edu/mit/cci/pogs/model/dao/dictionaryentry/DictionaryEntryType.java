package edu.mit.cci.pogs.model.dao.dictionaryentry;

public enum DictionaryEntryType {
    CORRECT('C',"Correct entry"),
    WRONG('E',"Wrong entry");

    private Character dictionaryEntryTypeChar;
    private String dictionaryEntryTypeDisplay;

    DictionaryEntryType(Character scoreboardDisplayTypeChar, String scoreboardDisplayType){
        this.dictionaryEntryTypeDisplay = scoreboardDisplayType;
        this.dictionaryEntryTypeChar = scoreboardDisplayTypeChar;
    }

    public Character getId(){
        return dictionaryEntryTypeChar;
    }

    public String getDescription() {
        return dictionaryEntryTypeDisplay;
    }

}
