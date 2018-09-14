package edu.mit.cci.pogs.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TodoListMessageContent {

    private CollaborationMessage.CollaborationType collaborationType;
    private String messageType;
    private String triggeredBy;
    private String triggeredData;
    private String triggeredData2;
    private List<TodoEntry> todoEntries;

    public TodoListMessageContent() {
        this.todoEntries = new ArrayList<>();
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

    public List<TodoEntry> getTodoEntries() {
        return todoEntries;
    }

    public CollaborationMessage.CollaborationType getCollaborationType() {
        return collaborationType;
    }

    public void setCollaborationType(CollaborationMessage.CollaborationType collaborationType) {
        this.collaborationType = collaborationType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    @JsonIgnore
    public void addTodoEntry(Long entryId, String entryText, Boolean entryMarkedDone,
                             List<String> assignedSubjects) {

        this.todoEntries.add(new TodoEntry(entryId, entryText, entryMarkedDone, assignedSubjects));
    }
    public JSONObject toJSON(){

        JSONObject jo = new JSONObject();
        jo.put("messageType",messageType);
        jo.put("triggeredBy",triggeredBy);
        jo.put("triggeredData",triggeredData);
        jo.put("triggeredData2",triggeredData2);

        JSONArray ja = new JSONArray();
        if(todoEntries!=null)
            for(TodoEntry vp: todoEntries){
                ja.add(vp.toJSON());
            }
        jo.put("todoEntries",ja);
        return jo;
    }
}

class TodoEntry {


    private Long entryId;
    private String entryText;
    private Boolean entryMarkedDone;

    private List<String> assignedSubjects;


    public TodoEntry(Long entryId, String entryText, Boolean entryMarkedDone, List<String> assignedSubjects) {
        this.entryId = entryId;
        this.entryText = entryText;
        this.entryMarkedDone = entryMarkedDone;
        this.assignedSubjects = assignedSubjects;
    }

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public Boolean getEntryMarkedDone() {
        return entryMarkedDone;
    }

    public void setEntryMarkedDone(Boolean entryMarkedDone) {
        this.entryMarkedDone = entryMarkedDone;
    }

    public List<String> getAssignedSubjects() {
        return assignedSubjects;
    }

    public void setAssignedSubjects(List<String> assignedSubjects) {
        this.assignedSubjects = assignedSubjects;
    }
    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        jo.put("entryId",entryId);
        jo.put("entryText",entryText);
        jo.put("entryMarkedDone",entryMarkedDone);

        JSONArray ja = new JSONArray();
        if(assignedSubjects!=null)
            for(String vp: assignedSubjects){
                ja.add(vp);
            }
        jo.put("assignedSubjects",ja);
        return jo;
    }
}
