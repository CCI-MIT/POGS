package edu.mit.cci.pogs.model.dao.executablescript;

public enum ScriptType {

    PERPETUAL_INIIT_CONDITION('P', "Condition script to start perpetual session");

    private Character typeChar;
    private String description;

    ScriptType(Character typeChar, String description){
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
