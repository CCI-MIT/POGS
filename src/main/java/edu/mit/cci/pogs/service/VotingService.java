package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.votingpool.VotingPoolDao;
import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;


@Service
public class VotingService {

    @Autowired
    private VotingPoolDao votingPoolDao;

    @Autowired
    private VotingPoolOptionDao votingPoolOptionDao;

    @Autowired
    private VotingPoolVoteDao votingPoolVoteDao;

    public void deleteVotingPoolByCompletedTaskId(Long completedTaskId) {
        for (VotingPool votingPool : votingPoolDao.listByCompletedTaskId(completedTaskId)) {
            deleteVotingPool(votingPool.getId());
        }
    }

    public void deleteVotingPool(Long votingPoolId) {

        List<VotingPoolOption> allVotingOptions = votingPoolOptionDao.listByVotingPoolId(votingPoolId);
        for (VotingPoolOption vpo : allVotingOptions) {
            deleteVotingPoolOption(vpo.getId());
        }
        votingPoolDao.delete(votingPoolId);
    }

    public void deleteVotingPoolOption(Long votingPoolOptionId) {
        List<VotingPoolVote> allVotingOptions = votingPoolVoteDao.listByPoolOptionId(votingPoolOptionId);
        for (VotingPoolVote vpv : allVotingOptions) {
            deleteVote(vpv.getId());
        }
        votingPoolOptionDao.delete(votingPoolOptionId);

    }

    public void deleteVote(Long votingPoolVoteId) {
        votingPoolVoteDao.delete(votingPoolVoteId);
    }
}
