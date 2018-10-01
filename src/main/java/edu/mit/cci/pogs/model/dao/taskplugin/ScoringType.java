package edu.mit.cci.pogs.model.dao.taskplugin;

public enum ScoringType {

    indexedAnswerOneAnswerKey('S', "Answer key"),
    externalService('L', "External service"),
    attributeIsScore('A',"Attribute is score");

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
