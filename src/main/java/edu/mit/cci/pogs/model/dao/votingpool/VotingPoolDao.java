package edu.mit.cci.pogs.model.dao.votingpool;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;

import java.util.List;

public interface VotingPoolDao extends Dao<VotingPool, Long> {

    List<VotingPool> get();

    List<VotingPool> listByCompletedTaskId(Long completedTaskId);

    void delete(Long votingPoolId);
}
