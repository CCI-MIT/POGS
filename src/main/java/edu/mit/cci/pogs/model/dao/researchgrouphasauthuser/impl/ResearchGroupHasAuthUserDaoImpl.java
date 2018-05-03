package edu.mit.cci.pogs.model.dao.researchgrouphasauthuser.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.researchgrouphasauthuser.ResearchGroupHasAuthUserDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.model.jooq.tables.records.ResearchGroupHasAuthUserRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.RESEARCH_GROUP_HAS_AUTH_USER;
 
@Repository
public class ResearchGroupHasAuthUserDaoImpl extends AbstractDao<ResearchGroupHasAuthUser, Long, ResearchGroupHasAuthUserRecord> implements ResearchGroupHasAuthUserDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public ResearchGroupHasAuthUserDaoImpl(DSLContext dslContext) {
        super(dslContext, RESEARCH_GROUP_HAS_AUTH_USER, RESEARCH_GROUP_HAS_AUTH_USER.ID, ResearchGroupHasAuthUser.class);
        this.dslContext = dslContext;
    }
 
    public List<ResearchGroupHasAuthUser> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(RESEARCH_GROUP_HAS_AUTH_USER).getQuery();
 
        return query.fetchInto(ResearchGroupHasAuthUser.class);
    }


    public List<ResearchGroupHasAuthUser> listByResearchGroup(Long researchGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(RESEARCH_GROUP_HAS_AUTH_USER).getQuery();

        query.addConditions(RESEARCH_GROUP_HAS_AUTH_USER.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(ResearchGroupHasAuthUser.class);
    }
    public List<ResearchGroupHasAuthUser> listByAuthUser(Long authUserId){

        final SelectQuery<Record> query = dslContext.select()
                .from(RESEARCH_GROUP_HAS_AUTH_USER).getQuery();

        query.addConditions(RESEARCH_GROUP_HAS_AUTH_USER.AUTH_USER_ID.eq(authUserId));

        return query.fetchInto(ResearchGroupHasAuthUser.class);
    }

    public void delete(ResearchGroupHasAuthUser rghau) {
        dslContext.delete(RESEARCH_GROUP_HAS_AUTH_USER)
                .where(RESEARCH_GROUP_HAS_AUTH_USER.ID.eq(rghau.getId()))
                .execute();

    }
}
 
