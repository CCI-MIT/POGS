package edu.mit.cci.pogs.model.dao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static edu.mit.cci.pogs.model.jooq.Tables.AUTH_USER;

@Repository
public class UserDao {

    private final DSLContext dslContext;

    public UserDao(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public AuthUser get(long id) {
        return dslContext.selectFrom(AUTH_USER)
                         .where(AUTH_USER.ID.eq(id))
                         .fetchOne().into(AuthUser.class);
    }

    public AuthUser get(String emailAddress) {
        return dslContext.selectFrom(AUTH_USER)
                         .where(AUTH_USER.EMAIL_ADDRESS.eq(emailAddress))
                         .fetchOne().into(AuthUser.class);
    }
}
