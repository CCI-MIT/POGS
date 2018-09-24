package edu.mit.cci.pogs.model.dao.subjectattribute;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
 
public interface SubjectAttributeDao extends Dao<SubjectAttribute, Long> {
 
    List<SubjectAttribute> list();

    List<SubjectAttribute> listBySubjectId(Long subjectId);

    void deleteBySubjectId(Long subjectId);
}
 
