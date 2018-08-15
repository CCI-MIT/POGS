package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectcommunication.SubjectCommunicationDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectCommunication;
import edu.mit.cci.pogs.view.session.beans.SubjectCommunicationBean;

@Service
public class SubjectCommunicationService {


    @Autowired
    private SubjectCommunicationDao subjectCommunicationDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private SubjectDao subjectDao;

    public void createSubjectCommunications(Long sessionId, boolean allowCommunication) {
        List<Subject> subjectList = subjectDao.listBySessionId(sessionId);
        for (Subject su : subjectList) {
            List<SubjectCommunication> currentCommunications = subjectCommunicationDao.listByFromSubjectId(su.getId());
            for (Subject sub2 : subjectList) {
                boolean alreadyHasCommunicationToSubject = false;
                if (currentCommunications == null || currentCommunications.size() == 0) {
                    createSubjectCommunication(su.getId(), sub2.getId(), allowCommunication);
                } else {
                    for (SubjectCommunication sc : currentCommunications) {
                        if (sc.getToSubjectId() == sub2.getId()) {
                            alreadyHasCommunicationToSubject = true;
                            break;
                        }
                    }
                    if (!alreadyHasCommunicationToSubject) {
                        createSubjectCommunication(su.getId(), sub2.getId(), allowCommunication);
                    }
                }
            }
        }

    }

    private void createSubjectCommunication(Long subjectFrom, Long subjectTo, boolean allowCommunication) {
        SubjectCommunication sc = new SubjectCommunication();
        sc.setFromSubjectId(subjectFrom);
        sc.setToSubjectId(subjectTo);
        sc.setAllowed(allowCommunication);
        subjectCommunicationDao.create(sc);
    }

    public void updateSubjectCommunications(SubjectCommunicationBean subjectCommunicationBean) {
        Session session = sessionDao.get(subjectCommunicationBean.getSessionId());

        List<Subject> subjectList = subjectDao.listBySessionId(session.getId());
        List<SubjectCommunication> toDisallow = new ArrayList<>();
        List<SubjectCommunication> toAllow = new ArrayList<>();
        for (Subject s : subjectList) {
            List<SubjectCommunication> currentCommunications = subjectCommunicationDao.listByFromSubjectId(s.getId());
            for (SubjectCommunication currentSubjectCommunication : currentCommunications) {
                boolean mustAllow = false;
                if (currentCommunications != null) {
                    for (String scStr : subjectCommunicationBean.getSelectedCommunications()) {
                        if (Long.parseLong(scStr) == currentSubjectCommunication.getId()) {
                            mustAllow = true;
                        }
                    }
                    if (mustAllow) {
                        toAllow.add(currentSubjectCommunication);
                    } else {
                        toDisallow.add(currentSubjectCommunication);
                    }
                }
            }
        }
        List<SubjectCommunication> finalToDisallow = new ArrayList<>(toDisallow);
        List<SubjectCommunication> finalToAllow = new ArrayList<>(toAllow);
        for (SubjectCommunication allowed : toAllow) {
            for (SubjectCommunication disallowed : toDisallow) {
                if (allowed.getFromSubjectId() == disallowed.getToSubjectId() &&
                        allowed.getToSubjectId() == disallowed.getFromSubjectId()) {
                    finalToAllow.add(disallowed);
                    finalToDisallow.remove(disallowed);
                }
            }
        }
        for (SubjectCommunication allowed : finalToAllow) {
            allowed.setAllowed(true);
            subjectCommunicationDao.update(allowed);
        }
        for (SubjectCommunication disallow : finalToDisallow) {
            disallow.setAllowed(false);
            subjectCommunicationDao.update(disallow);
        }
    }
}
