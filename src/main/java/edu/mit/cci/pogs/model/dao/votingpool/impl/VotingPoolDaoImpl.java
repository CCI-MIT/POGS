package edu.mit.cci.pogs.model.dao.votingpool.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.votingpool.VotingPoolDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;
import edu.mit.cci.pogs.model.jooq.tables.records.VotingPoolRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.VOTING_POOL;

@Repository
public class VotingPoolDaoImpl extends AbstractDao<VotingPool, Long, VotingPoolRecord> implements VotingPoolDao {
    private DSLContext dslContext;

    @Autowired
    public VotingPoolDaoImpl(DSLContext dslContext) {
        super(dslContext, VOTING_POOL, VOTING_POOL.ID, VotingPool.class);
        this.dslContext = dslContext;
    }

    @Override
    public List<VotingPool> get() {
        final SelectQuery<Record> query = dslContext.select()
                .from(VOTING_POOL).getQuery();

        return query.fetchInto(VotingPool.class);
    }


    @Override
    public List<VotingPool> listByCompletedTaskId(Long completedTaskId) {
        final SelectQuery<Record> query = dslContext.select()
                .from(VOTING_POOL).getQuery();

        query.addConditions(VOTING_POOL.COMPLETED_TASK_ID.eq(completedTaskId));
        return query.fetchInto(VotingPool.class);
    }

    public void delete(Long votingPoolId) {
        dslContext.delete(VOTING_POOL)
                .where(VOTING_POOL.ID.eq(votingPoolId))
                .execute();

    }
}

