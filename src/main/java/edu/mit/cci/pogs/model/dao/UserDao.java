package edu.mit.cci.pogs.model.dao;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;

public interface UserDao extends Dao<AuthUser, Long> {

    AuthUser get(String emailAddress);
}
