package edu.mit.cci.pogs.model.dao.task;

public enum ScoringType {

    RIGHT_ANSWER_BASED('S', "Sequential, fixed order"),
    PARALLEL_RANDOM_ORDER('L', "Parallel, randomized");

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
