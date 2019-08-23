package edu.mit.cci.pogs.model.dao.studyhasresearchgroup.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.studyhasresearchgroup.StudyHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.records.StudyHasResearchGroupRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.STUDY_HAS_RESEARCH_GROUP;
 
@Repository
public class StudyHasResearchGroupDaoImpl extends AbstractDao<StudyHasResearchGroup, Long, StudyHasResearchGroupRecord> implements StudyHasResearchGroupDao {
 
    private final DSLContext dslContext;
 
    @Autowired
    public StudyHasResearchGroupDaoImpl(DSLContext dslContext) {
        super(dslContext, STUDY_HAS_RESEARCH_GROUP, STUDY_HAS_RESEARCH_GROUP.ID, StudyHasResearchGroup.class);
        this.dslContext = dslContext;
    }
 
    public List<StudyHasResearchGroup> list(){
 
        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY_HAS_RESEARCH_GROUP).getQuery();
 
        return query.fetchInto(StudyHasResearchGroup.class);
    }

    public List<StudyHasResearchGroup> listByStudyId(Long studyId){

        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY_HAS_RESEARCH_GROUP).getQuery();
            query.addOrderBy(STUDY_HAS_RESEARCH_GROUP.STUDY_ID);
            query.addConditions(STUDY_HAS_RESEARCH_GROUP.STUDY_ID.eq(studyId));
        return query.fetchInto(StudyHasResearchGroup.class);
    }

    public List<StudyHasResearchGroup> listByResearchGroup(Long researchGroupId){

        final SelectQuery<Record> query = dslContext.select()
                .from(STUDY_HAS_RESEARCH_GROUP).getQuery();
                query.addConditions(STUDY_HAS_RESEARCH_GROUP.RESEARCH_GROUP_ID.eq(researchGroupId));
        return query.fetchInto(StudyHasResearchGroup.class);
    }

    public void delete(StudyHasResearchGroup rghau) {
        dslContext.delete(STUDY_HAS_RESEARCH_GROUP)
                .where(STUDY_HAS_RESEARCH_GROUP.ID.eq(rghau.getId()))
                .execute();

    }
 
}
 
