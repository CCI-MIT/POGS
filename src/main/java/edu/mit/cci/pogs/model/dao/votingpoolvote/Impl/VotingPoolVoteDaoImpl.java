package edu.mit.cci.pogs.model.dao.votingpoolvote.Impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.model.jooq.tables.records.VotingPoolVoteRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.VOTING_POOL_VOTE;

@Repository
public class VotingPoolVoteDaoImpl extends AbstractDao<VotingPoolVote, Long, VotingPoolVoteRecord> implements VotingPoolVoteDao {

    private DSLContext dslContext;

    @Autowired
    public VotingPoolVoteDaoImpl(DSLContext dslContext) {
        super(dslContext, VOTING_POOL_VOTE, VOTING_POOL_VOTE.ID, VotingPoolVote.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<VotingPoolVote> get() {
        final SelectQuery<Record> query = dslContext.select(VOTING_POOL_VOTE.fields())
                .from(VOTING_POOL_VOTE)
                .getQuery();

        return query.fetchInto(VotingPoolVote.class);
    }
}
