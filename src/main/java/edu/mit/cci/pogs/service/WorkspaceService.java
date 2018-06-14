package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Service
public class WorkspaceService {

    @Autowired
    private SubjectDao subjectDao;

    public Subject getSubject(String externalId){
        return subjectDao.getByExternalId(externalId);
    }
    private void changeDisplayName(){

    }
    private void getTeamRoster(){

    }


}
