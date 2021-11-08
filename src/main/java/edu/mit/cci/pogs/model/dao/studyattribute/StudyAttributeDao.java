package edu.mit.cci.pogs.model.dao.studyattribute;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyAttribute;

public interface StudyAttributeDao  extends Dao<StudyAttribute, Long> {
     List<StudyAttribute> listBySessionId(Long sessionId);
     List<StudyAttribute> listByStudyId(Long studyId);
}
