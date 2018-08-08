package edu.mit.cci.pogs.model.dao.subjecthaschannel;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;
 
public interface SubjectHasChannelDao extends Dao<SubjectHasChannel, Long> {
 
    List<SubjectHasChannel> list();
    List<SubjectHasChannel> listBySubjectId(Long subjectId);
    List<SubjectHasChannel> listByChatId(Long chatId);
    void delete(SubjectHasChannel rghau);
}
 
