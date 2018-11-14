package edu.mit.cci.pogs.model.dao.completedtaskscore;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;

import java.util.List;

public interface CompletedTaskScoreDao extends Dao<CompletedTaskScore, Long> {
    CompletedTaskScore getByCompletedTaskId(Long completedTaskScoreId);
    void deleteByCompletedTaskId(Long completedTaskId);
    List<CompletedTaskScore> listByCompletedTasksIds(List<Long> completedTaskIds);
}
