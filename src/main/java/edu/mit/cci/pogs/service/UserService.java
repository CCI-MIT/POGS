package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.researchgrouphasauthuser.ResearchGroupHasAuthUserDao;
import edu.mit.cci.pogs.model.dao.user.UserDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.view.auth.beans.RegisterBean;
import edu.mit.cci.pogs.view.authuser.AuthUserBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    private ResearchGroupHasAuthUserDao researchGroupHasAuthUserDao;

    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder, ResearchGroupHasAuthUserDao researchGroupHasAuthUserDao) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.researchGroupHasAuthUserDao = researchGroupHasAuthUserDao;
    }

    public AuthUser createUser(RegisterBean registerBean) {
        AuthUser authUser = new AuthUser();
        authUser.setEmailAddress(registerBean.getEmailAddress());
        authUser.setPassword(passwordEncoder.encode(registerBean.getPassword()));
        authUser.setFirstName(registerBean.getFirstName());
        authUser.setLastName(registerBean.getLastName());
        return userDao.create(authUser);
    }



    public AuthUser adminCreateOrUpdateUser(AuthUserBean authUserBean) {
        AuthUser authUser = new AuthUser();
        authUser.setEmailAddress(authUserBean.getEmailAddress());

        authUser.setFirstName(authUserBean.getFirstName());
        authUser.setLastName(authUserBean.getLastName());
        authUser.setIsAdmin(authUserBean.getAdmin());
        authUser.setId(authUserBean.getId());

        if(authUser.getId()==null) {

            authUser.setPassword(passwordEncoder.encode(authUserBean.getPassword()));
            authUser = userDao.create(authUser);
            authUserBean.setId(authUser.getId());
            createUserGroups(authUserBean);
            return  authUser;

        }else{
            userDao.update(authUser);
            uddateUserGroups(authUserBean);
            return authUser;
        }

    }
    private void createUserGroups(AuthUserBean authUserBean){

    }
    private void uddateUserGroups(AuthUserBean authUserBean){
        if(authUserBean.getResearchGroupRelationshipBean()==null && authUserBean.getResearchGroupRelationshipBean().getSelectedValues()==null){
            return;
        }
        List<ResearchGroupHasAuthUser> toCreate =  new ArrayList<>();
        List<ResearchGroupHasAuthUser> toDelete =  new ArrayList<>();
        List<ResearchGroupHasAuthUser> currentlySelected =  listResearchGroupHasAuthUserByAuthUser(authUserBean.getId());

        for(String researchGroupId : authUserBean.getResearchGroupRelationshipBean().getSelectedValues()){
            
        }

    }
    public List<ResearchGroupHasAuthUser> listResearchGroupHasAuthUserByAuthUser(Long authUserId){
        return researchGroupHasAuthUserDao.listByAuthUser(authUserId);
    }

}
