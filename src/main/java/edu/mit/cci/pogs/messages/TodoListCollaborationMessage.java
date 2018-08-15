package edu.mit.cci.pogs.messages;

import java.util.List;

public class TodoListCollaborationMessage extends PogsMessage<TodoListMessageContent> {
}

class TodoListMessageContent{
    private String triggeredBy;
    private String triggeredData;
    private String triggeredData2;
    private List<TodoEntry> todoEntries;

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

    public void setTodoEntries(List<TodoEntry> todoEntries) {
        this.todoEntries = todoEntries;
    }
}
class TodoEntry{
    private Long entryId;
    private String entryText;
    private Boolean entryMarkedDone;

    private List<String> assignedSubjects;

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
}
