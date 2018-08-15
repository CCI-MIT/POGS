package edu.mit.cci.pogs.model.dao.chatchannel;
 
import java.util.List;
 
import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatChannel;
 
public interface ChatChannelDao extends Dao<ChatChannel, Long> {
 
    List<ChatChannel> list();
    List<ChatChannel> listBySessionId(Long sessionId);
    void delete(ChatChannel rghau);
}
 
