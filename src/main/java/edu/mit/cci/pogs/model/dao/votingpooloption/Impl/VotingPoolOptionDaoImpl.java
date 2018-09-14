package edu.mit.cci.pogs.model.dao.votingpooloption.Impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;
import edu.mit.cci.pogs.model.jooq.tables.records.VotingPoolOptionRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_HAS_CHANNEL;
import static edu.mit.cci.pogs.model.jooq.Tables.VOTING_POOL_OPTION;
import static edu.mit.cci.pogs.model.jooq.Tables.VOTING_POOL_VOTE;

@Repository
public class VotingPoolOptionDaoImpl extends AbstractDao<VotingPoolOption, Long, VotingPoolOptionRecord> implements VotingPoolOptionDao{

    private DSLContext dslContext;

    @Autowired
    public VotingPoolOptionDaoImpl(DSLContext dslContext) {
        super(dslContext, VOTING_POOL_OPTION, VOTING_POOL_OPTION.ID, VotingPoolOption.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<VotingPoolOption> list() {
        final SelectQuery<Record> query = dslContext.select()
                .from(VOTING_POOL_OPTION).getQuery();

        return query.fetchInto(VotingPoolOption.class);
    }

    @Override
    public List<VotingPoolOption> listByVotingPoolId(Long votingPoolId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(VOTING_POOL_OPTION).getQuery();

        query.addConditions(VOTING_POOL_OPTION.VOTING_POOL_ID.eq(votingPoolId));
        return query.fetchInto(VotingPoolOption.class);
    }

    public void delete(Long votingPoolId) {
        dslContext.delete(VOTING_POOL_OPTION)
                .where(VOTING_POOL_OPTION.ID.eq(votingPoolId))
                .execute();
    }

    public void deleteByVotingPoolId(Long votingPoolId) {
        dslContext.delete(VOTING_POOL_OPTION)
                .where(VOTING_POOL_OPTION.VOTING_POOL_ID.eq(votingPoolId))
                .execute();

    }
}
