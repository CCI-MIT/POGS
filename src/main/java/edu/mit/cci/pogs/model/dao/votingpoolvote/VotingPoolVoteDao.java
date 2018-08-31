package edu.mit.cci.pogs.model.dao.votingpoolvote;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;

import java.util.List;

public interface VotingPoolVoteDao extends Dao<VotingPoolVote, Long> {

    List<VotingPoolVote> list();
    Integer countVote(Long votingPoolId);
    VotingPoolVote getBySubjectId(Long subjectId);
    List<VotingPoolVote> listByPoolOptionId(Long optionId);
    void delete(Long votingPoolId);
}
