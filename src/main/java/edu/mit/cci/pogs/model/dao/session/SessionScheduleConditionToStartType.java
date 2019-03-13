package edu.mit.cci.pogs.model.dao.session;

public enum SessionScheduleConditionToStartType {

    NUMBER_OF_USERS_CHECKED_IN('N', "Number of users checked in"),
    CONDITION_SCRIPT('S', "Defined by conditioning script");

    private Character typeChar;
    private String description;

    SessionScheduleConditionToStartType(Character typeChar, String description){
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
