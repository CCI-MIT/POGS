package edu.mit.cci.pogs.model.dao.individualsubjectscore;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;

public interface IndividualSubjectScoreDao extends Dao<IndividualSubjectScore, Long> {
    IndividualSubjectScore getByGiven(Long subjectId, Long completedTaskId);
    List<IndividualSubjectScore> findByGiven( Long completedTaskId);
}
