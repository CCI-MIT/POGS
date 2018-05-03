package edu.mit.cci.pogs.model.dao.researchgrouphasauthuser;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
 
public interface ResearchGroupHasAuthUserDao extends Dao<ResearchGroupHasAuthUser, Long> {
 
    List<ResearchGroupHasAuthUser> list();

    List<ResearchGroupHasAuthUser> listByAuthUser(Long authUserId);
    List<ResearchGroupHasAuthUser> listByResearchGroup(Long researchGroupId);

    void delete(ResearchGroupHasAuthUser rghau);
}
 
