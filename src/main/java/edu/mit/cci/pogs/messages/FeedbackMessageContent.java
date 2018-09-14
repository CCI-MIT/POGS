package edu.mit.cci.pogs.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FeedbackMessageContent {

    private List<CompletedTasksFeedback> completedTasks;

    private CollaborationMessage.CollaborationType collaborationType;


    FeedbackMessageContent(Map<String, Integer> subjectsParticipations, Long completedTaskId) {


        this.collaborationType = CollaborationMessage.CollaborationType.FEEDBACK_BAR;
        Iterator<String> subjectsExternalIds = subjectsParticipations.keySet().iterator();
        CompletedTasksFeedback ctf = new CompletedTasksFeedback();
        ctf.setCompletedTaskId(completedTaskId);
        ctf.setSubjectFeedbacks(new ArrayList<>());
        Integer totalIteractions = 0;
        while (subjectsExternalIds.hasNext()) {
            String externalId = subjectsExternalIds.next();
            Integer subjInter = subjectsParticipations.get(externalId);
            SubjectFeedback sf = new SubjectFeedback();
            sf.setExternalId(externalId);
            sf.setInteraction(subjInter);
            totalIteractions += subjInter;
            ctf.getSubjectFeedbacks().add(sf);
        }
        ctf.setTotalInteractions(totalIteractions);
        for (SubjectFeedback sef : ctf.getSubjectFeedbacks()) {
            sef.setPercentage((sef.getInteraction() / (float) ctf.getTotalInteractions()) * 100);
        }
        this.completedTasks = new ArrayList<>();
        this.completedTasks.add(ctf);


    }

    public CollaborationMessage.CollaborationType getCollaborationType() {
        return collaborationType;
    }

    public void setCollaborationType(CollaborationMessage.CollaborationType collaborationType) {
        this.collaborationType = collaborationType;
    }

    public List<CompletedTasksFeedback> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(List<CompletedTasksFeedback> completedTasks) {
        this.completedTasks = completedTasks;
    }

}

class CompletedTasksFeedback {

    private Long completedTaskId;
    private List<SubjectFeedback> subjectFeedbacks;
    private Integer totalInteractions;

    public Long getCompletedTaskId() {
        return completedTaskId;
    }

    public void setCompletedTaskId(Long completedTaskId) {
        this.completedTaskId = completedTaskId;
    }

    public List<SubjectFeedback> getSubjectFeedbacks() {
        return subjectFeedbacks;
    }

    public void setSubjectFeedbacks(List<SubjectFeedback> subjectFeedbacks) {
        this.subjectFeedbacks = subjectFeedbacks;
    }

    public Integer getTotalInteractions() {
        return totalInteractions;
    }

    public void setTotalInteractions(Integer totalInteractions) {
        this.totalInteractions = totalInteractions;
    }

}

class SubjectFeedback {
    private String externalId;
    private Integer interaction;
    private Float percentage;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getInteraction() {
        return interaction;
    }

    public void setInteraction(Integer interaction) {
        this.interaction = interaction;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
}
