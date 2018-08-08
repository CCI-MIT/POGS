package edu.mit.cci.pogs.view.session.beans;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatChannel;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionHasTaskGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;

public class ChatChannelBean extends ChatChannel {

    public ChatChannelBean(){

    }

    private Long studyId;

    public ChatChannelBean(ChatChannel chatChannel){
        super(chatChannel);
    }

    private List<Subject> subjectList;

    private List<SubjectHasChannel> subjectHasChannelList;

    private String[] selectedValues;

    public List<SubjectHasChannel> getSubjectHasChannelList() {
        return subjectHasChannelList;
    }

    public void setSubjectHasChannelList(List<SubjectHasChannel> subjectHasChannelList) {

        if (subjectHasChannelList != null && !subjectHasChannelList.isEmpty()) {
            List<String> selectedValues = new ArrayList<>();
            for (SubjectHasChannel rghau : subjectHasChannelList) {
                selectedValues.add(rghau.getSubjectId().toString());
            }
            this.selectedValues = selectedValues.toArray(new String[0]);
        }

    }
    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(List<Subject> subjectList) {
        this.subjectList = subjectList;
    }

    public String[] getSelectedValues() {
        return selectedValues;
    }

    public void setSelectedValues(String[] selectedValues) {
        this.selectedValues = selectedValues;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }
}
