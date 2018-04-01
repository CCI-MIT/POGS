package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.UserDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.view.auth.beans.RegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser createUser(RegisterBean registerBean) {
        AuthUser authUser = new AuthUser();
        authUser.setEmailAddress(registerBean.getEmailAddress());
        authUser.setPassword(passwordEncoder.encode(registerBean.getPassword()));
        authUser.setFirstName(registerBean.getFirstName());
        authUser.setLastName(registerBean.getLastName());
        return userDao.create(authUser);
    }
}
