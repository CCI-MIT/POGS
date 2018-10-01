package edu.mit.cci.pogs.model.dao.taskplugin;

public class ScoringConfiguration {

    private ScoringType scoringType;
    private AnswerKeyFormat answerKeyFormat;
    private String answerKeyPrefix;

    private String answerSheetPrefix;

    private String url;

    public ScoringType getScoringType() {
        return scoringType;
    }

    public void setScoringType(ScoringType scoringType) {
        this.scoringType = scoringType;
    }

    public AnswerKeyFormat getAnswerKeyFormat() {
        return answerKeyFormat;
    }

    public void setAnswerKeyFormat(AnswerKeyFormat answerKeyFormat) {
        this.answerKeyFormat = answerKeyFormat;
    }

    public String getAnswerKeyPrefix() {
        return answerKeyPrefix;
    }

    public void setAnswerKeyPrefix(String answerKeyPrefix) {
        this.answerKeyPrefix = answerKeyPrefix;
    }

    public String getAnswerSheetPrefix() {
        return answerSheetPrefix;
    }

    public void setAnswerSheetPrefix(String answerSheetPrefix) {
        this.answerSheetPrefix = answerSheetPrefix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
