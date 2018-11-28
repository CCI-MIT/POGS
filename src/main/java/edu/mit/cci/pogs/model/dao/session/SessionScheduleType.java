package edu.mit.cci.pogs.model.dao.session;


public enum SessionScheduleType {
    SCHEDULED_DATE('S', "Scheduled to a specific date"),
    PERPETUAL('P', "Perpetual for a period of time");

    private Character typeChar;
    private String description;

    SessionScheduleType(Character typeChar, String description){
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
