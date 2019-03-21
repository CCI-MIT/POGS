package edu.mit.cci.pogs.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.chatchannel.ChatChannelDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.subjectcommunication.SubjectCommunicationDao;
import edu.mit.cci.pogs.model.dao.subjecthaschannel.SubjectHasChannelDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatChannel;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectCommunication;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;

@Service
public class SubjectService {


    @Autowired
    private SubjectCommunicationDao subjectCommunicationDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private SubjectHasChannelDao subjectHasChannelDao;

    @Autowired
    private ChatChannelDao chatChannelDao;

    public JSONArray getSubjectsSubjectIsAllowedToTalkJson(Long id) {

        List<SubjectCommunication> subjectCommunications =
                subjectCommunicationDao.listByFromSubjectId(id);

        JSONArray allowedToTalkTo = new JSONArray();
        if (subjectCommunications != null) {
            for (SubjectCommunication sc : subjectCommunications) {
                Subject subject = subjectDao.get(sc.getToSubjectId());
                if (sc.getAllowed()) {
                    if (sc.getToSubjectId() != sc.getFromSubjectId()) {
                        allowedToTalkTo.put(subject.getSubjectExternalId());
                    }
                }
            }
        }
        return allowedToTalkTo;
    }

    public JSONArray getChannelsSubjectIsIn(Long subjectId) {
        List<SubjectHasChannel> subjectHasChannels = subjectHasChannelDao.listBySubjectId(subjectId);
        JSONArray channelSubjectIsIn = new JSONArray();
        if (subjectHasChannels != null) {
            for (SubjectHasChannel shc : subjectHasChannels) {
                ChatChannel chatChannel = chatChannelDao.get(shc.getChatChannelId());
                channelSubjectIsIn.put(chatChannel.getChannelName());
            }
        }
        return channelSubjectIsIn;
    }

    public void createOrUpdateSubjectsAttributes(String attributesToAddJson) {
        if (attributesToAddJson != null) {

            JSONArray array = new JSONArray(attributesToAddJson);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jo = array.getJSONObject(i);
                    String extId = jo.getString("externalId");
                    Subject su = subjectDao.getByExternalId(extId);

                    if(su!=null) {
                        JSONArray attributes = jo.getJSONArray("attributes");
                        for(int j = 0 ; j < attributes.length(); j ++){
                            SubjectAttribute sa = new SubjectAttribute();
                            JSONObject att = attributes.getJSONObject(j);
                            sa.setAttributeName(att.getString("attributeName"));
                            sa.setSubjectId(su.getId());
                            if (att.has("stringValue")) {
                                sa.setStringValue(att.getString("stringValue"));
                            }
                            if (att.has("integerValue")) {
                                sa.setIntegerValue(new Integer(att.getInt("integerValue")).longValue());
                            }
                            if (att.has("doubleValue")) {
                                sa.setRealValue(att.getDouble("doubleValue"));
                            }
                            boolean alreadyExist = false;
                            List<SubjectAttribute> list = subjectAttributeDao.listBySubjectId(su.getId());
                            for (SubjectAttribute teaz : list) {
                                if (teaz.getAttributeName().equals(sa.getAttributeName())) {
                                    teaz.setStringValue(sa.getStringValue());
                                    teaz.setIntegerValue(sa.getIntegerValue());
                                    teaz.setRealValue(sa.getRealValue());
                                    subjectAttributeDao.update(teaz);
                                    alreadyExist = true;
                                }
                            }
                            if (!alreadyExist) {
                                subjectAttributeDao.create(sa);
                            }
                        }


                    }
                }
            }
        }
    }
}
