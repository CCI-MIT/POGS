package edu.mit.cci.pogs.model.dao.task;

public enum ScoringType {

    RIGHT_ANSWER_BASED('S', "Answer key"),
    EXTERNAL_SERVICE('L', "External service");

    private Character scoringTypeChar;
    private String scoringType;

    ScoringType(Character scoringTypeChar, String scoringType){
        this.scoringType = scoringType;
        this.scoringTypeChar = scoringTypeChar;
    }

    public Character getId(){
        return scoringTypeChar;
    }
    public String getScoringType(){
        return scoringType;
    }
}
