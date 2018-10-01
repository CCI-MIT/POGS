package edu.mit.cci.pogs.model.dao.taskplugin;

public class TaskPluginProperties {

    private String name;
    private String description;

    private ScoringConfiguration scoring;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ScoringConfiguration getScoring() {
        return scoring;
    }

    public void setScoring(ScoringConfiguration scoring) {
        this.scoring = scoring;
    }
}
