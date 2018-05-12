package edu.mit.cci.pogs.model.dao.session;

public enum TeamCreationType {

    SINGLE('S', "Single team exclusive"),
    MULTIPLE('C', "Multiple teams");


    private Character teamCreationTypeChar;
    private String teamCreationType;

    TeamCreationType(Character teamCreationTypeChar, String teamCreationType){
        this.teamCreationType = teamCreationType;
        this.teamCreationTypeChar = teamCreationTypeChar;
    }

    public Character getId(){
        return teamCreationTypeChar;
    }
    public String getTeamCreationType(){
        return teamCreationType;
    }

}
