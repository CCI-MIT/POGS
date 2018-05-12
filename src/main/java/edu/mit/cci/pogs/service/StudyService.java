package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.dao.studyhasresearchgroup.StudyHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroupHasAuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyHasResearchGroup;
import edu.mit.cci.pogs.view.authuser.AuthUserBean;
import edu.mit.cci.pogs.view.study.beans.StudyBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {

    private StudyDao studyDao;

    private StudyHasResearchGroupDao studyHasResearchGroupDao;

    @Autowired
    public StudyService(StudyDao studyDao, StudyHasResearchGroupDao studyHasResearchGroupDao) {
        this.studyDao = studyDao;
        this.studyHasResearchGroupDao = studyHasResearchGroupDao;
    }

    public List<StudyHasResearchGroup> listResearchGroupHasAuthUserByAuthUser(Long studyId){
        return this.studyHasResearchGroupDao.listByStudyId(studyId);
    }

    public Study createOrUpdate(StudyBean studyBean) {

        Study study = new Study();
        study.setId(studyBean.getId());
        study.setStudyDescription(studyBean.getStudyDescription());
        study.setStudyName(studyBean.getStudyName());
        study.setStudySessionPrefix(studyBean.getStudySessionPrefix());

        if(study.getId() == null){
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
            if(!foundRGH){
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
            if(!selectedAlreadyIn){
                StudyHasResearchGroup rghau = new StudyHasResearchGroup();
                rghau.setStudyId(studyBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for(StudyHasResearchGroup toCre: toCreate){
            studyHasResearchGroupDao.create(toCre);
        }
        for(StudyHasResearchGroup toDel: toDelete){
            studyHasResearchGroupDao.delete(toDel);
        }

    }

    private List<StudyHasResearchGroup> listStudyHasResearchGroupByStudy(Long studyId) {
        return studyHasResearchGroupDao.listByStudyId(studyId);
    }


}
