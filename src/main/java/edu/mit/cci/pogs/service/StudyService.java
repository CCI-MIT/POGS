package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.studyhasresearchgroup.StudyHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.view.study.beans.SessionBean;
import edu.mit.cci.pogs.view.study.beans.StudyBean;

@Service
public class StudyService {

    private final StudyDao studyDao;

    private final StudyHasResearchGroupDao studyHasResearchGroupDao;



    private final SessionDao sessionDao;

    @Autowired
    public StudyService(StudyDao studyDao, StudyHasResearchGroupDao studyHasResearchGroupDao
                        , SessionDao sessionDao) {
        this.studyDao = studyDao;
        this.studyHasResearchGroupDao = studyHasResearchGroupDao;

        this.sessionDao = sessionDao;
    }

    public List<StudyHasResearchGroup> listStudyHasResearchGroupByStudyId(Long studyId) {
        return this.studyHasResearchGroupDao.listByStudyId(studyId);
    }

    public Study createOrUpdate(StudyBean studyBean) {

        Study study = new Study();

        ObjectUtils.Copy(study, studyBean);

        if (study.getId() == null) {
            study = studyDao.create(study);
            studyBean.setId(study.getId());
            createOrUpdateUserGroups(studyBean);
        } else {
            studyDao.update(study);
            createOrUpdateUserGroups(studyBean);
        }
        return study;

    }

    private void createOrUpdateUserGroups(StudyBean studyBean) {
        if (studyBean.getResearchGroupRelationshipBean() == null && studyBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<StudyHasResearchGroup> toCreate = new ArrayList<>();
        List<StudyHasResearchGroup> toDelete = new ArrayList<>();
        List<StudyHasResearchGroup> currentlySelected = listStudyHasResearchGroupByStudy(studyBean.getId());

        for (StudyHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : studyBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }

        for (String researchGroupId : studyBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (StudyHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                StudyHasResearchGroup rghau = new StudyHasResearchGroup();
                rghau.setStudyId(studyBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (StudyHasResearchGroup toCre : toCreate) {
            studyHasResearchGroupDao.create(toCre);
        }
        for (StudyHasResearchGroup toDel : toDelete) {
            studyHasResearchGroupDao.delete(toDel);
        }

    }

    private List<StudyHasResearchGroup> listStudyHasResearchGroupByStudy(Long studyId) {
        return studyHasResearchGroupDao.listByStudyId(studyId);
    }


    public List<SessionBean> groupBessionsByBaseSessions(Long studyId) {
        List<Session> sessions = listSessionsByStudyId(studyId);
        HashMap<Long, SessionBean> baseSessions = new LinkedHashMap<>();
        for(Session s: sessions){
            if(s.getParentSessionId()== null) {
                baseSessions.put(s.getId(),new SessionBean(s));
            }
        }

        for(Session s: sessions){
            if(s.getParentSessionId()!= null) {
                SessionBean base = baseSessions.get(s.getParentSessionId());
                if(base != null ) {
                    base.getChildSessions().add(s);
                }
            }
        }
        List<SessionBean> baseSessionList = new ArrayList();
        if(baseSessions.values()!=null) {
            for (SessionBean sb : baseSessions.values()) {
                baseSessionList.add(sb);
            }
        }

        return baseSessionList;

    }

    public List<Session> listSessionsByStudyId(Long studyId) {
        return sessionDao.listByStudyId(studyId);

    }

}
