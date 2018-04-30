package edu.mit.cci.pogs.model.dao.user.impl;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.user.UserDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;

import edu.mit.cci.pogs.model.jooq.tables.records.AuthUserRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static edu.mit.cci.pogs.model.jooq.Tables.AUTH_USER;


@Repository
public class UserDaoImpl extends AbstractDao<AuthUser, Long, AuthUserRecord> implements UserDao {

    private final DSLContext dslContext;

    @Autowired
    public UserDaoImpl(DSLContext dslContext) {
        super(dslContext, AUTH_USER, AUTH_USER.ID, AuthUser.class);
        this.dslContext = dslContext;
    }

    public AuthUser get(String emailAddress) {
        return dslContext.selectFrom(AUTH_USER)
                         .where(AUTH_USER.EMAIL_ADDRESS.eq(emailAddress))
                         .fetchOne().into(AuthUser.class);
    }

    public List<AuthUser> list(){
        final SelectQuery<Record> query = dslContext.select()
                .from(AUTH_USER).getQuery();

        return query.fetchInto(AuthUser.class);
    }
}
