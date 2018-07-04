package edu.mit.cci.pogs.model.dao.team.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.model.jooq.tables.records.TeamRecord;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM;
import static edu.mit.cci.pogs.model.jooq.Tables.TEAM_HAS_SUBJECT;

@Repository
public class TeamDaoImpl extends AbstractDao<Team, Long, TeamRecord> implements TeamDao {

    private final DSLContext dslContext;

    @Autowired
    public TeamDaoImpl(DSLContext dslContext) {
        super(dslContext, TEAM, TEAM.ID, Team.class);
        this.dslContext = dslContext;
    }

    public List<Team> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM).getQuery();

        return query.fetchInto(Team.class);
    }

    public List<Team> listByRoundId(Long id) {
        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM).getQuery();
        query.addConditions(TEAM.ROUND_ID.eq(id));

        return query.fetchInto(Team.class);
    }

    public Team getByRoundIdTaskId(Long roundId, Long taskId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(TEAM).getQuery();
        query.addConditions(TEAM.ROUND_ID.eq(roundId));
        if (taskId != null) {
            query.addConditions(TEAM.TASK_ID.eq(taskId));
        }


        Record record = query.fetchOne();
        if (record == null) {
            return null;
        } else {
            return record.into(Team.class);
        }
    }

    public void deleteByRoundId(Long roundId) {
        dslContext.delete(TEAM)
                .where(TEAM.ROUND_ID.eq(roundId))
                .execute();
    }

    public Team getSubjectTeam(Long subjectId, Long sessionId, Long roundId, Long taskId) {
        final SelectQuery<Record> query = dslContext.select(TEAM.fields())
                .from(SUBJECT)
                .join(TEAM_HAS_SUBJECT)
                .on(TEAM_HAS_SUBJECT.SUBJECT_ID.eq(SUBJECT.ID))
                .join(TEAM)
                .on(TEAM.ID.eq(TEAM_HAS_SUBJECT.TEAM_ID))
                .getQuery();

        query.addConditions(SUBJECT.ID.eq(subjectId));
        if (sessionId != null) {
            query.addConditions(TEAM.SESSION_ID.eq(sessionId));
        }

        if (roundId != null) {
            query.addConditions(TEAM.ROUND_ID.eq(roundId));
        }
        if (taskId != null) {
            query.addConditions(TEAM.TASK_ID.eq(taskId));
        }
        Record record = query.fetchOne();

        if (record == null) {
            return null;
        } else {
            return record.into(Team.class);
        }

    }
}
 
