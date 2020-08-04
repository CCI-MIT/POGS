package edu.mit.cci.pogs.service.export.exportBeans;

import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

public class SessionRelatedScore {

    private Session session;

    private Subject subject;

    private List<String> taskNames;

    private List<Double> taskIndividualScores;

    private List<Double> taskGroupScores;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public List<String> getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(List<String> taskNames) {
        this.taskNames = taskNames;
    }

    public List<Double> getTaskIndividualScores() {
        return taskIndividualScores;
    }

    public void setTaskIndividualScores(List<Double> taskIndividualScores) {
        this.taskIndividualScores = taskIndividualScores;
    }

    public List<Double> getTaskGroupScores() {
        return taskGroupScores;
    }

    public void setTaskGroupScores(List<Double> taskGroupScores) {
        this.taskGroupScores = taskGroupScores;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
