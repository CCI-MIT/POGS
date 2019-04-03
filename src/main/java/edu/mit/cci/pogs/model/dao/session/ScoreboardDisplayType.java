package edu.mit.cci.pogs.model.dao.session;

public enum ScoreboardDisplayType {

    //DISPLAY_TASK('T',"Show scoreboard at end of each task"),
   //DISPLAY_ROUND('R',"Show scoreboard at end of each round"),
    NO_DISPLAY_SESSION('N',"Do not display"),
    DISPLAY_SESSION('S',"Show scoreboard at end of session");

    private Character scoreboardDisplayTypeChar;
    private String scoreboardDisplayType;

    ScoreboardDisplayType(Character scoreboardDisplayTypeChar, String scoreboardDisplayType){
        this.scoreboardDisplayType = scoreboardDisplayType;
        this.scoreboardDisplayTypeChar = scoreboardDisplayTypeChar;
    }

    public Character getId(){
        return scoreboardDisplayTypeChar;
    }
    public String getScoreboardDisplayType(){
        return scoreboardDisplayType;
    }

}
