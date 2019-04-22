package edu.mit.cci.pogs.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.utils.ColorUtils;

@Service
public class TeamService {


    @Autowired
    private TeamDao teamDao;

    @Autowired
    private TeamHasSubjectDao teamHasSubjectDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;



    public List<Subject> getTeamSubjectsFromCompletedTask(Long subjectId, Long teamId) {
        if(teamId != null) {
            Team team = teamDao.get(teamId);

            List<Subject> ret = getSubjectsFromTeam(team);
            return ret;
        } else {
            return getTeamSubjects(subjectId,null,null,null);

        }


    }

    public List<Subject> getTeamSubjects(Long subjectId, Long sessionId, Long roundId, Long taskId) {

        Team team = teamDao.getSubjectTeam(subjectId,sessionId,roundId,taskId);
        List<Subject> ret = getSubjectsFromTeam(team);
        if (ret != null) return ret;
        return null;
    }

    private List<Subject> getSubjectsFromTeam(Team team) {
        if(team!=null) {
            List<Subject> ret = new ArrayList<>();
            for (TeamHasSubject teamHasSub : teamHasSubjectDao.listByTeamId(team.getId())) {
                Subject su = subjectDao.get(teamHasSub.getSubjectId());
                if (su != null) {
                    ret.add(su);
                }
            }
            return ret;
        }
        return null;
    }

    public List<Subject> getTeamMates(Task task, Subject su, Round round) {
        List<Subject> teammates = getTeamSubjects(su.getId(), su.getSessionId(),
                round.getId(), task.getId());

        if (teammates == null || teammates.size() == 0) {
            teammates = getTeamSubjects(su.getId(), su.getSessionId(),
                    round.getId(), null);
        }
        if (teammates == null || teammates.size() == 0) {
            teammates = getTeamSubjects(su.getId(), su.getSessionId(),
                    null, null);
        }
        return teammates;
    }


    public JSONObject getSubjectJsonObject(Subject s) {
        JSONObject subject = new JSONObject();
        subject.put("externalId", s.getSubjectExternalId());
        subject.put("displayName", s.getSubjectDisplayName());
        JSONArray subjectAttributes = new JSONArray();
        List<SubjectAttribute> attributes = subjectAttributeDao.listBySubjectId(s.getId());
        for (SubjectAttribute sa : attributes) {
            JSONObject att = new JSONObject();
            att.put("attributeName", sa.getAttributeName());
            att.put("stringValue", sa.getStringValue());
            att.put("integerValue", sa.getIntegerValue());
            att.put("realValue", sa.getRealValue());
            subjectAttributes.put(att);
        }
        subject.put("attributes", subjectAttributes);
        return subject;
    }
    public JSONArray getTeamatesJSONObject(List<Subject> teammates) {
        JSONArray ja = new JSONArray();
        for (Subject s : teammates) {
            JSONObject subject = getSubjectJsonObject(s);
            ja.put(subject);
        }
        return ja;
    }

    public Subject generateFakeSubject(String subjectExternalId) {
        Subject su = new Subject();
        su.setSubjectExternalId(subjectExternalId);
        su.setSubjectDisplayName(subjectExternalId);
        return su;
    }

    public Team getTeamCascadeConfig(Long subjId,Long sessionId, Long roundId, Long taskId) {
        Team team = teamDao.getSubjectTeam(subjId, sessionId, roundId, taskId);
        if (team == null) {
            team = teamDao.getSubjectTeam(subjId, sessionId, roundId, null);
            if (team == null) {
                team = teamDao.getSubjectTeam(subjId, sessionId, null, null);
            }
        }
        return team;
    }

    public JSONArray getFakeSubjectCanTalkTo(){
        JSONArray ja = new JSONArray();
        ja.put("su01");
        ja.put("su02");
        ja.put("su03");
        ja.put("su04");
        return ja;
    }

    public JSONArray getFakeTeamatesJSONObject() {
        List<Subject> teammates = new ArrayList<>();
        teammates.add(generateFakeSubject("su01"));
        teammates.add(generateFakeSubject("su02"));
        teammates.add(generateFakeSubject("su03"));
        teammates.add(generateFakeSubject("su04"));

        JSONArray ja = new JSONArray();
        Color[] colors = ColorUtils.generateVisuallyDistinctColors(
                10,
                ColorUtils.MIN_COMPONENT, ColorUtils.MAX_COMPONENT);
        int colorIndex = 0;
        for (Subject s : teammates) {
            JSONObject subject = new JSONObject();
            subject.put("externalId", s.getSubjectExternalId());
            subject.put("displayName", s.getSubjectDisplayName());

            JSONArray subjectAttributes = new JSONArray();

            JSONObject att = new JSONObject();
            Color color = colors[colorIndex];
            att.put("attributeName", ColorUtils.SUBJECT_DEFAULT_BACKGROUND_COLOR_ATTRIBUTE_NAME);

            att.put("stringValue",
                    String.format("#%02x%02x%02x", color.getRed(),
                            color.getGreen(), color.getBlue())
            );
            subjectAttributes.put(att);

            att = new JSONObject();

            color = ColorUtils.generateFontColorBasedOnBackgroundColor(colors[colorIndex]);
            att.put("attributeName", ColorUtils.SUBJECT_DEFAULT_FONT_COLOR_ATTRIBUTE_NAME);
            att.put("stringValue", String.format("#%02x%02x%02x", color.getRed(),
                    color.getGreen(), color.getBlue()));
            colorIndex++;

            subjectAttributes.put(att);

            att = new JSONObject();
            att.put("attributeName", "age");
            att.put("stringValue", "43");
            subjectAttributes.put(att);

            att = new JSONObject();
            att.put("attributeName", "education");
            att.put("stringValue", "Graduate");
            subjectAttributes.put(att);


            subject.put("attributes", subjectAttributes);
            ja.put(subject);
        }
        return ja;
    }


}
