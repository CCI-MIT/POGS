package edu.mit.cci.pogs.model.dao.subject;

public enum NameGenerationType {
    GREEK_LETTERS('G', "Greek letters names"),
    USER('U', "User01 , user02");

    private Character typeChar;
    private String description;

    NameGenerationType(Character typeChar, String description){
        this.description = description;
        this.typeChar = typeChar;
    }

    public Character getId(){
        return typeChar;
    }
    public String getDescription(){
        return description;
    }
}
