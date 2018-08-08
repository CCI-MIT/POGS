package edu.mit.cci.pogs.model.dao.subjectcommunication;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectCommunication;
 
public interface SubjectCommunicationDao extends Dao<SubjectCommunication, Long> {
 
    List<SubjectCommunication> list();
    List<SubjectCommunication> listByFromSubjectId(Long fromSubjectId);
}
 
