package edu.mit.cci.pogs.service;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import edu.mit.cci.pogs.view.session.beans.SubjectBean;
import edu.mit.cci.pogs.view.session.beans.SubjectsBean;

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

    public Subject createSubjectSafeExternalId(Subject subject){

        int attempt = 1;
        boolean createdSubject = false;
        while(!createdSubject) {
            if (subjectDao.getByExternalId(subject.getSubjectExternalId()) == null) {
                subject.setCreatedAt(new Timestamp(new Date().getTime()));
                return subjectDao.create(subject);
            } else {
                subject.setSubjectExternalId(subject.getSubjectExternalId()+"_"+attempt);
                attempt++;
            }
        }
        return null;
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
                                if(!att.isNull("stringValue")) {
                                    sa.setStringValue(att.getString("stringValue"));
                                } else {
                                    sa.setStringValue("");
                                }
                            }
                            if (att.has("integerValue")) {
                                sa.setIntegerValue(new Integer(att.getInt("integerValue")).longValue());
                            }
                            if (att.has("doubleValue")) {
                                sa.setRealValue(att.getDouble("doubleValue"));
                            }
                            if(att.has("internalAttribute")){
                                sa.setInternalAttribute(true);
                            } else {
                                sa.setInternalAttribute(false);
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

    public void createOrUpdateSubject(SubjectsBean subjectsBean){
        List<Subject> currentSubjectList = subjectDao.listBySessionId(subjectsBean.getSessionId());
        List<SubjectBean> subjectListToCreateOrUpdate = new ArrayList<>();
        List<Subject> subjectListToDelete = new ArrayList<>();
        List<SubjectBean> subjectBeanList = subjectsBean.getSubjectList();

        if(subjectBeanList!=null){
            for(SubjectBean sb: subjectBeanList) {
                if(!sb.isNullEntry()) {
                    subjectListToCreateOrUpdate.add(sb);
                }
            }
            for(Subject current: currentSubjectList){
                boolean shouldDelete = true;
                for(Subject selected: subjectListToCreateOrUpdate){
                    if(selected.getId()!=null) {
                        if (selected.getId().longValue() == (current.getId().longValue())){
                            shouldDelete = false;
                        }
                    }
                }
                if(shouldDelete){
                    subjectListToDelete.add(current);
                }
            }
        }else {
            currentSubjectList.stream().forEach(e->subjectListToDelete.add(e));
        }

        for(SubjectBean createOrUpdate: subjectListToCreateOrUpdate){
            createOrUpdate.setSessionId(subjectsBean.getSessionId());
            if (createOrUpdate.getId() != null) {
                subjectDao.update(createOrUpdate);
            } else {
                subjectDao.create(createOrUpdate);
            }
            List<SubjectAttribute> subjectAttributes = createOrUpdate.getSubjectAttributes();
            this.createOrUpdateSubjectAttributes(subjectAttributes, createOrUpdate.getId());

        }
        subjectListToDelete.stream().forEach(a -> this.deleteSubject(a.getId()));


    }

    public List<SubjectAttribute> getSubjectAttributes(Long subjectId){
        List<SubjectAttribute> currentAttributes = this.subjectAttributeDao.listBySubjectId(subjectId);
        return currentAttributes;
    }
    public void createOrUpdateSubjectAttributes(List<SubjectAttribute> updatedSubjectAttributes, Long subjectId){
        List<SubjectAttribute> currentAttributes = this.subjectAttributeDao.listBySubjectId(subjectId);
        List<SubjectAttribute> subjectAttributeListToCreateOrUpdate = new ArrayList<>();
        List<SubjectAttribute> subjectAttributeListToDelete = new ArrayList<>();
        if(updatedSubjectAttributes!=null){
            for(SubjectAttribute curr : updatedSubjectAttributes){
                if(!isNullSubjectAttr(curr)) {
                    subjectAttributeListToCreateOrUpdate.add(curr);
                }
            }

            for(SubjectAttribute currentSa: currentAttributes){
                boolean shouldDelete = true;
                for(SubjectAttribute updated: subjectAttributeListToCreateOrUpdate){
                    if(updated.getId()!=null){
                        if(updated.getId().longValue() == currentSa.getId().longValue()){
                            shouldDelete = false;
                        }
                    }
                }
                if(shouldDelete){
                    subjectAttributeListToDelete.add(currentSa);
                }
            }
        }else{
            currentAttributes.stream().forEach(e -> subjectAttributeListToDelete.add(e));
        }
        for (SubjectAttribute subjectAttribute : subjectAttributeListToCreateOrUpdate) {
            subjectAttribute.setSubjectId(subjectId);
            if (subjectAttribute.getId() != null) {
                subjectAttributeDao.update(subjectAttribute);
            } else {
                subjectAttributeDao.create(subjectAttribute);
            }
        }
        subjectAttributeListToDelete.stream().forEach(i->this.subjectAttributeDao.delete(i.getId()));

    }

    private boolean isNullSubjectAttr(SubjectAttribute curr) {
        if(curr.getId()==null &&
        curr.getInternalAttribute() == null&&
        curr.getLatest() == null &&
        curr.getAttributeName() == null &&
        curr.getIntegerValue() == null &&
        curr.getRealValue() == null &&
        curr.getStringValue() == null &&
        curr.getSubjectId()== null){
            return true;
        }
        return false;

    }

    public void deleteSubject(Long subjectId){
        this.subjectCommunicationDao.deleteBySubjectId(subjectId);
        this.subjectAttributeDao.deleteBySubjectId(subjectId);
        this.subjectDao.delete(subjectId);
    }
}
