package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;

@Service
public class TeamService {


    @Autowired
    private TeamDao teamDao;

    @Autowired
    private TeamHasSubjectDao teamHasSubjectDao;

    @Autowired
    private SubjectDao subjectDao;


    public List<Subject> getTeamSubjects(Long subjectId, Long sessionId, Long roundId, Long taskId) {

        Team team = teamDao.getSubjectTeam(subjectId,sessionId,roundId,taskId);
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
}
