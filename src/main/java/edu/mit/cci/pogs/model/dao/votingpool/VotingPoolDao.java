package edu.mit.cci.pogs.model.dao.votingpool;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;
import java.util.List;

public interface VotingPoolDao extends Dao<VotingPool, Long>{
//        VotingPool get();
        List<VotingPool> get();
    }
