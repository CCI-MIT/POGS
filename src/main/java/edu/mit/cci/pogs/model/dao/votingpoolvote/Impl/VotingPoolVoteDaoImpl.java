package edu.mit.cci.pogs.model.dao.votingpoolvote.Impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.model.jooq.tables.records.VotingPoolVoteRecord;

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
    public List<VotingPoolVote> list() {
        final SelectQuery<Record> query = dslContext.select(VOTING_POOL_VOTE.fields())
                .from(VOTING_POOL_VOTE)
                .getQuery();

        return query.fetchInto(VotingPoolVote.class);
    }

    @Override
    public Integer countVote(Long votingPoolId) {
        return dslContext.select(DSL.count())
                .from(VOTING_POOL_VOTE)
                .where(VOTING_POOL_VOTE.VOTING_POOL_OPTION_ID.eq(votingPoolId))
                .fetchOne(0, int.class);
    }

    public List<VotingPoolVote> listByPoolOptionId(Long optionId) {

        final SelectQuery<Record> query = dslContext.select(VOTING_POOL_VOTE.fields())
                .from(VOTING_POOL_VOTE)
                .getQuery();
        query.addConditions(VOTING_POOL_VOTE.VOTING_POOL_OPTION_ID.eq(optionId));
        return query.fetchInto(VotingPoolVote.class);
    }

    public VotingPoolVote getBySubjectId(Long subjectId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(VOTING_POOL_VOTE).getQuery();
        query.addConditions(VOTING_POOL_VOTE.SUBJECT_ID.eq(subjectId));
        Record record = query.fetchOne();
        if (record == null) {
            return null;
        } else {
            return record.into(VotingPoolVote.class);
        }
    }

    public void delete(Long votingPoolId) {
        dslContext.delete(VOTING_POOL_VOTE)
                .where(VOTING_POOL_VOTE.ID.eq(votingPoolId))
                .execute();
    }
}
