package edu.mit.cci.pogs.model.dao.session;

public enum CommunicationConstraint {

    GROUP_CHAT('G',"Group chat channel - No constraint"),
    MATRIX_CHAT('M',"Matrix chat - Constraint: matrix"),
    DYADIC_CHAT('D',"Dyadic communication - Constraint telephone system");

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
