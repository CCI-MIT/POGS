package edu.mit.cci.pogs.model.dao.completedtask;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
 
public interface CompletedTaskDao extends Dao<CompletedTask, Long> {
 
    List<CompletedTask> list();

    List<CompletedTask> listByRoundId(Long roundId);

    CompletedTask getByRoundIdTaskIdTeamId(Long roundId, Long teamId, Long taskId);

    CompletedTask getBySubjectIdTaskId(Long subjectId, Long taskId);

    void deleteByRoundId(Long roundId);

    List<CompletedTask> listByCompletedTaskIds(List<Long> completedTaskIds);
}
 
