package edu.mit.cci.pogs.model.dao.researchgroup.impl;
 
import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.ResearchGroupRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
import static edu.mit.cci.pogs.model.jooq.Tables.RESEARCH_GROUP;
 
@Repository
public class ResearchGroupDaoImpl extends AbstractDao<ResearchGroup, Long, ResearchGroupRecord> implements ResearchGroupDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public ResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, RESEARCH_GROUP, RESEARCH_GROUP.ID, ResearchGroup.class);
        this.dslContext = dslContext;
    }
 
    public List<ResearchGroup> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(RESEARCH_GROUP).getQuery();
 
        return query.fetchInto(ResearchGroup.class);
    }
 
}
 
