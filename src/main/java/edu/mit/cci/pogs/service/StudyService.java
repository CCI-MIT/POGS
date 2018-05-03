package edu.mit.cci.pogs.service;

import edu.mit.cci.pogs.model.dao.study.StudyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyService {

    private StudyDao studyDao;

    @Autowired
    public StudyService(StudyDao studyDao) {
        this.studyDao = studyDao;
    }

}
