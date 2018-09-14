package edu.mit.cci.pogs.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VotingPoolMessageContent {

    private List<VotingPool> votingPools;
    private String triggeredBy;
    private String triggeredData;
    private String triggeredData2;


    private CollaborationMessage.CollaborationType collaborationType;
    private String messageType;


    public VotingPoolMessageContent(){
        this.votingPools = new ArrayList<>();
    }

    public CollaborationMessage.CollaborationType getCollaborationType() {
        return collaborationType;
    }

    public void setCollaborationType(CollaborationMessage.CollaborationType collaborationType) {
        this.collaborationType = collaborationType;
    }

    public List<VotingPool> getVotingPools() {
        return votingPools;
    }

    public void setVotingPools(List<VotingPool> votingPools) {
        this.votingPools = votingPools;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getTriggeredData() {
        return triggeredData;
    }

    public void setTriggeredData(String triggeredData) {
        this.triggeredData = triggeredData;
    }

    public String getTriggeredData2() {
        return triggeredData2;
    }

    public void setTriggeredData2(String triggeredData2) {
        this.triggeredData2 = triggeredData2;
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("triggeredBy",triggeredBy);
        jo.put("triggeredData",triggeredData);
        jo.put("triggeredData2",triggeredData2);

        JSONArray ja = new JSONArray();
        if(votingPools!=null)
        for(VotingPool vp: votingPools){
            ja.add(vp.toJSON());
        }
        jo.put("votingPools",ja);
        return jo;
    }
    @JsonIgnore
    public void addVotingPool(edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool votingPool,
                              List<edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption> votingPoolOptions,
                              List<Integer> voteCounts, Long totalOfVotes) {
        VotingPool vp = new VotingPool();
        vp.setVotingPoolId(votingPool.getId());
        vp.setVotingQuestion(votingPool.getVotingQuestion());
        if(votingPoolOptions!= null ) {
            for (int i = 0; i < votingPoolOptions.size(); i++) {
                edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption vpo = votingPoolOptions.get(i);
                VotingPoolOption votingPoolOption = new VotingPoolOption();
                votingPoolOption.setVotingPoolOptionId(vpo.getId());
                votingPoolOption.setVotingOption(vpo.getVotingOption());
                votingPoolOption.setVotes(voteCounts.get(i));
                if(totalOfVotes == 0){
                    votingPoolOption.setPercentage(0f);
                }else {
                    votingPoolOption.setPercentage(100*((float) votingPoolOption.getVotes() / (float) totalOfVotes));
                }
                vp.getVotingOptions().add(votingPoolOption);
            }
        }
        votingPools.add(vp);
    }
}
class VotingPool {
    private Long votingPoolId;
    private String votingQuestion;
    private List<VotingPoolOption> votingOptions;

    public VotingPool(){
        this.votingOptions = new ArrayList<>();
    }
    public Long getVotingPoolId() {
        return votingPoolId;
    }

    public void setVotingPoolId(Long votingPoolId) {
        this.votingPoolId = votingPoolId;
    }

    public String getVotingQuestion() {
        return votingQuestion;
    }

    public void setVotingQuestion(String votingQuestion) {
        this.votingQuestion = votingQuestion;
    }

    public List<VotingPoolOption> getVotingOptions() {
        return votingOptions;
    }

    public void setVotingOptions(List<VotingPoolOption> votingOptions) {
        this.votingOptions = votingOptions;
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("votingPoolId",votingPoolId);
        jo.put("votingQuestion",votingQuestion);
        JSONArray ja = new JSONArray();
        if(votingOptions!=null) {
            for (VotingPoolOption vpo : votingOptions){
                ja.add(vpo.toJSON());
            }
        }
        jo.put("votingOptions",ja);
        return jo;
    }
}

class VotingPoolOption{
    private Long votingPoolOptionId;

    private String votingOption;

    private Integer votes;

    private Float percentage;

    public Long getVotingPoolOptionId() {
        return votingPoolOptionId;
    }

    public void setVotingPoolOptionId(Long votingPoolOptionId) {
        this.votingPoolOptionId = votingPoolOptionId;
    }

    public String getVotingOption() {
        return votingOption;
    }

    public void setVotingOption(String votingOption) {
        this.votingOption = votingOption;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("votingPoolOptionId",votingPoolOptionId);
        jo.put("votingOption",votingOption);
        jo.put("votes",votes);
        jo.put("percentage",percentage);
        return jo;
    }
}