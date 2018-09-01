package edu.mit.cci.pogs.model.dao.session;

public enum TeamCreationTime {

    BEGINING_SESSION('S', "Creation at beginning of session"),
    //BEGINING_ROUND('C', "Creation at the beginning of each round"),
    BEGINING_TASK('E', "Creation at the beginning of each task");


    private Character teamCreationTimeChar;
    private String teamCreationTime;

    TeamCreationTime(Character teamCreationTimeChar, String teamCreationTime){
        this.teamCreationTime = teamCreationTime;
        this.teamCreationTimeChar = teamCreationTimeChar;
    }

    public Character getId(){
        return teamCreationTimeChar;
    }
    public String getTeamCreationTime(){
        return teamCreationTime;
    }

}