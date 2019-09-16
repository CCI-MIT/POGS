package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.studyhasresearchgroup.StudyHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.view.study.beans.SessionBean;
import edu.mit.cci.pogs.view.study.beans.StudyBean;

@Service
public class StudyService extends ServiceBase {

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

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<StudyHasResearchGroup> currentlySelected = listStudyHasResearchGroupByStudy(studyBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(StudyHasResearchGroup::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = studyBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            StudyHasResearchGroup rghau = new StudyHasResearchGroup();
            rghau.setStudyId(studyBean.getId());
            rghau.setResearchGroupId(toCre);
            studyHasResearchGroupDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            StudyHasResearchGroup rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getStudyId() == studyBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            studyHasResearchGroupDao.delete(rghau);
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
