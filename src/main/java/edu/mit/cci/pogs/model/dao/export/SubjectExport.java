package edu.mit.cci.pogs.model.dao.export;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;

public class SubjectExport {

    private Map<String, SubjectAttribute> attributeMap;


    private String studyPrefix;

    private String sessionSuffix;

    private Timestamp sessionStartDate;

    private String teamId;

    private Subject subject;

    private String lastCheckInPage;

    private String lastCheckInTime;

    private List<String> attributes;


    public SubjectExport(Subject su) {
        this.subject = su;
        this.attributeMap = new HashMap<>();
    }

    public void addSubjectAttribute(SubjectAttribute sa) {
        this.attributeMap.put(sa.getAttributeName(), sa);
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getStudyPrefix() {
        return studyPrefix;
    }

    public void setStudyPrefix(String studyPrefix) {
        this.studyPrefix = studyPrefix;
    }

    public String getSessionSuffix() {
        return sessionSuffix;
    }

    public void setSessionSuffix(String sessionSuffix) {
        this.sessionSuffix = sessionSuffix;
    }

    public String getSubjectId() {
        return subject.getId().toString();
    }


    public String getSubjectExternalId() {
        return subject.getSubjectExternalId();
    }


    public String getSubjectDisplayName() {
        return subject.getSubjectDisplayName();
    }


    public String getSubjectPreviousSessionSubjectId() {
        if(subject.getPreviousSessionSubject()!=null){
            return subject.getPreviousSessionSubject().toString();
        } else {
            return  "";
        }
    }

    public String getLastCheckInPage() {
        return lastCheckInPage;
    }

    public void setLastCheckInPage(String lastCheckInPage) {
        this.lastCheckInPage = lastCheckInPage;
    }

    public String getLastCheckInTime() {
        return lastCheckInTime;
    }

    public void setLastCheckInTime(String lastCheckInTime) {
        this.lastCheckInTime = lastCheckInTime;
    }

    public String getExtraColumns() {
        String ret = "";
        for (String column : this.attributes) {
            SubjectAttribute sa = this.attributeMap.get(column);
            if (sa != null) {
                if (sa.getStringValue() != null) {
                    ret += sa.getStringValue() + ";";
                } else {
                    if (sa.getIntegerValue() != null) {
                        ret += sa.getIntegerValue() + ";";
                    } else {
                        if (sa.getRealValue() != null) {
                            ret += sa.getRealValue() + ";";
                        }
                    }
                }
            } else {
                ret += ";";
            }

        }
        return ret;
    }

    public Timestamp getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(Timestamp sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }
}
