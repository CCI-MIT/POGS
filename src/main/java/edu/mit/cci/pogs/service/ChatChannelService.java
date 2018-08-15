package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.chatchannel.ChatChannelDao;
import edu.mit.cci.pogs.model.dao.subjecthaschannel.SubjectHasChannelDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatChannel;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectHasChannel;
import edu.mit.cci.pogs.view.session.beans.ChatChannelBean;

@Service
public class ChatChannelService {

    @Autowired
    private SubjectHasChannelDao subjectHasChannelDao;

    @Autowired
    private ChatChannelDao chatChannelDao;

    public void updateOrCreateChat(ChatChannelBean chatChannelBean) {

        if (chatChannelBean.getSelectedValues() == null ) {
            return;
        }
        List<SubjectHasChannel> toCreate = new ArrayList<>();
        List<SubjectHasChannel> toDelete = new ArrayList<>();
        List<SubjectHasChannel> currentlySelected = subjectHasChannelDao.listByChatId(chatChannelBean.getId());

        for (SubjectHasChannel rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : chatChannelBean.getSelectedValues()) {
                if (rghau.getSubjectId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }

        for (String taskGroupId : chatChannelBean.getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (SubjectHasChannel rghau : currentlySelected) {
                if (rghau.getSubjectId().longValue() == new Long(taskGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                SubjectHasChannel rghau = new SubjectHasChannel();
                rghau.setChatChannelId(chatChannelBean.getId());
                rghau.setSubjectId(new Long(taskGroupId));
                toCreate.add(rghau);
            }

        }
        for (SubjectHasChannel toCre : toCreate) {
            subjectHasChannelDao.create(toCre);
        }
        for (SubjectHasChannel toDel : toDelete) {
            subjectHasChannelDao.delete(toDel);
        }

    }

    public void delete(ChatChannel cc) {
        List<SubjectHasChannel> currentlySelected = subjectHasChannelDao.listByChatId(cc.getId());

        if(currentlySelected!=null) {
            for (SubjectHasChannel shc: currentlySelected){
                subjectHasChannelDao.delete(shc);
            }
        }
        chatChannelDao.delete(cc);
    }
}
