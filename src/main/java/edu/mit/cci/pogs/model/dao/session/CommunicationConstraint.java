package edu.mit.cci.pogs.model.dao.session;

public enum CommunicationConstraint {
    NO_CHAT('N',"No Chat"),

    GROUP_CHAT('G',"Group Chat Channel - No constraint"),
    MATRIX_CHAT('M',"Matrix Chat - Constraint: matrix"),
    DYADIC_CHAT('D',"Dyadic Communication - Constraint telephone system"),
    VIDEO_CHAT('V',"Video Group Chat Channel ");

    private Character communicationTypeChar;
    private String communicationType;

    CommunicationConstraint(Character communicationTypeChar, String communicationType){
        this.communicationType = communicationType;
        this.communicationTypeChar = communicationTypeChar;
    }

    public Character getId(){
        return communicationTypeChar;
    }
    public String getCommunicationType(){
        return communicationType;
    }
}
