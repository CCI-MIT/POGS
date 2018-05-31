package edu.mit.cci.pogs.model.dao.votingpooloption;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;

import java.util.List;

public interface VotingPoolOptionDao extends Dao<VotingPoolOption, Long> {
    List<VotingPoolOption> list();
}
