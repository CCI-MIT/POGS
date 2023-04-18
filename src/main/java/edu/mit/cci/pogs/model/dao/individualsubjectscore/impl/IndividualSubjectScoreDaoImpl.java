package edu.mit.cci.pogs.model.dao.individualsubjectscore.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.individualsubjectscore.IndividualSubjectScoreDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.records.IndividualSubjectScoreRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.INDIVIDUAL_SUBJECT_SCORE;
import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT;

@Repository
public class IndividualSubjectScoreDaoImpl extends AbstractDao<IndividualSubjectScore, Long,
        IndividualSubjectScoreRecord> implements IndividualSubjectScoreDao {

    private final DSLContext dslContext;

    @Autowired
    public IndividualSubjectScoreDaoImpl(DSLContext dslContext) {
        super(dslContext, INDIVIDUAL_SUBJECT_SCORE, INDIVIDUAL_SUBJECT_SCORE.ID, IndividualSubjectScore.class);
        this.dslContext = dslContext;
    }


    @Override
    public IndividualSubjectScore getByGiven(Long subjectId, Long completedTaskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(INDIVIDUAL_SUBJECT_SCORE).getQuery();
        query.addConditions(INDIVIDUAL_SUBJECT_SCORE.SUBJECT_ID.eq(subjectId));
        query.addConditions(INDIVIDUAL_SUBJECT_SCORE.COMPLETED_TASK_ID.eq(completedTaskId));
        Record record =  query.fetchAny();
        if(record == null) {
            return null;
        }else{
            return record.into(IndividualSubjectScore.class);
        }

    }
    @Override
    public List<IndividualSubjectScore> findByGiven(Long completedTaskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(INDIVIDUAL_SUBJECT_SCORE).getQuery();
        query.addConditions(INDIVIDUAL_SUBJECT_SCORE.COMPLETED_TASK_ID.eq(completedTaskId));
        List<IndividualSubjectScore> record =  query.fetchInto(IndividualSubjectScore.class);
        if(record == null) {
            return null;
        }else{
            return record;
        }

    }
}
