package edu.mit.cci.pogs.model.dao.session;

public enum SessionStatus {
    NOTSTARTED('P', "Not Started"),
    CONFIGURING('C', "Configuring"),
    ERROR('E', "Error"),
    STARTED('S', "Started"),
    DONE('D', "Done");

    private Character statusChar;
    private String status;

    SessionStatus(Character statusChar, String status){
        this.status = status;
        this.statusChar = statusChar;
    }

    public Character getId(){
        return statusChar;
    }
    public String getStatus(){
        return status;
    }

}
