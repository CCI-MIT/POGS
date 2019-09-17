package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.researchgrouphasauthuser.ResearchGroupHasAuthUserDao;
import edu.mit.cci.pogs.model.dao.user.UserDao;

import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.ObjectUtils;
import edu.mit.cci.pogs.view.auth.beans.RegisterBean;
import edu.mit.cci.pogs.view.authuser.beans.AuthUserBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceBase {

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
        authUser.setIsAdmin(false);

        if(userDao.get(authUser.getEmailAddress())!= null){
            return null;
        }
        return userDao.create(authUser);
    }


    public AuthUser adminCreateOrUpdateUser(AuthUserBean authUserBean) {
        AuthUser authUser = new AuthUser();


        ObjectUtils.Copy(authUser, authUserBean);

        if (authUser.getId() == null) {

            authUser.setPassword(passwordEncoder.encode(authUserBean.getPassword()));
            authUser = userDao.create(authUser);
            authUserBean.setId(authUser.getId());
            createOrUpdateUserGroups(authUserBean);
            return authUser;

        } else {
            userDao.update(authUser);
            createOrUpdateUserGroups(authUserBean);
            return authUser;
        }

    }


    private void createOrUpdateUserGroups(AuthUserBean authUserBean) {
        if (authUserBean.getResearchGroupRelationshipBean() == null && authUserBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<ResearchGroupHasAuthUser> currentlySelected = listResearchGroupHasAuthUserByAuthUser(authUserBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(ResearchGroupHasAuthUser::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = authUserBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            ResearchGroupHasAuthUser rghau = new ResearchGroupHasAuthUser();
            rghau.setAuthUserId(authUserBean.getId());
            rghau.setResearchGroupId(toCre);
            researchGroupHasAuthUserDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            ResearchGroupHasAuthUser rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getAuthUserId() == authUserBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            researchGroupHasAuthUserDao.delete(rghau);
        }

    }

    public List<ResearchGroupHasAuthUser> listResearchGroupHasAuthUserByAuthUser(Long authUserId) {
        return researchGroupHasAuthUserDao.listByAuthUser(authUserId);
    }

}
