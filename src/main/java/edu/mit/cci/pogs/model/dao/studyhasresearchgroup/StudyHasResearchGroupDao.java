package edu.mit.cci.pogs.model.dao.studyhasresearchgroup;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
 
public interface StudyHasResearchGroupDao extends Dao<StudyHasResearchGroup, Long> {
 
    List<StudyHasResearchGroup> list();
    List<StudyHasResearchGroup> listByStudyId(Long chatScriptId);
    List<StudyHasResearchGroup> listByResearchGroup(Long researchGroupId);
    void delete(StudyHasResearchGroup rghau);
}

 
