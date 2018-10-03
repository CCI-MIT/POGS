package edu.mit.cci.pogs.model.dao.completedtaskscore;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;

public interface CompletedTaskScoreDao extends Dao<CompletedTaskScore, Long> {
    CompletedTaskScore getByCompletedTaskId(Long completedTaskScoreId);
}
