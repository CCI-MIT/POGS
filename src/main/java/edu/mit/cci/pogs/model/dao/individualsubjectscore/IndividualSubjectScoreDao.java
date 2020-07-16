package edu.mit.cci.pogs.model.dao.individualsubjectscore;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;

public interface IndividualSubjectScoreDao extends Dao<IndividualSubjectScore, Long> {
    IndividualSubjectScore getByGiven(Long subjectId, Long completedTaskId);
}
