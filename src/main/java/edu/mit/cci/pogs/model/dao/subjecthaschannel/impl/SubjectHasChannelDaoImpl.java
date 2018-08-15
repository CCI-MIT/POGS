package edu.mit.cci.pogs.model.dao.subjecthaschannel.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.subjecthaschannel.SubjectHasChannelDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;
import edu.mit.cci.pogs.model.jooq.tables.records.SubjectHasChannelRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.SUBJECT_HAS_CHANNEL;

@Repository
public class SubjectHasChannelDaoImpl extends AbstractDao<SubjectHasChannel, Long, SubjectHasChannelRecord> implements SubjectHasChannelDao {

    private final DSLContext dslContext;

    @Autowired
    public SubjectHasChannelDaoImpl(DSLContext dslContext) {
        super(dslContext, SUBJECT_HAS_CHANNEL, SUBJECT_HAS_CHANNEL.ID, SubjectHasChannel.class);
        this.dslContext = dslContext;
    }

    public List<SubjectHasChannel> list() {

        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_HAS_CHANNEL).getQuery();

        return query.fetchInto(SubjectHasChannel.class);
    }

    public List<SubjectHasChannel> listBySubjectId(Long subjectId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_HAS_CHANNEL).getQuery();

        query.addConditions(SUBJECT_HAS_CHANNEL.SUBJECT_ID.eq(subjectId));
        return query.fetchInto(SubjectHasChannel.class);
    }

    public List<SubjectHasChannel> listByChatId(Long chatId) {

        final SelectQuery<Record> query = dslContext.select()
                .from(SUBJECT_HAS_CHANNEL).getQuery();


        query.addConditions(SUBJECT_HAS_CHANNEL.CHAT_CHANNEL_ID.eq(chatId));
        query.addOrderBy(SUBJECT_HAS_CHANNEL.SUBJECT_ID);
        return query.fetchInto(SubjectHasChannel.class);
    }

    public void delete(SubjectHasChannel rghau) {
        dslContext.delete(SUBJECT_HAS_CHANNEL)
                .where(SUBJECT_HAS_CHANNEL.ID.eq(rghau.getId()))
                .execute();

    }

}
 
