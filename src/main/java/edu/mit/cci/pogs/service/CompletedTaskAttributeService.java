package edu.mit.cci.pogs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;

@Service
public class CompletedTaskAttributeService {

    @Autowired
    private CompletedTaskAttributeDao completedTaskAttributeDao;

    public void createOrUpdate(String attributeName, String stringValue, Double doubleValue,
                               Long integerValue, Long completedTaskId,String extraData, boolean mustCreateNewAttribute) {

        CompletedTaskAttribute cta = completedTaskAttributeDao
                .getByAttributeNameCompletedTaskId(attributeName, completedTaskId);
        if(cta == null || mustCreateNewAttribute) {
            cta = new CompletedTaskAttribute();
            cta.setAttributeName(attributeName);
            cta.setCompletedTaskId(completedTaskId);
        }

        cta.setExtraData(extraData);
        cta.setStringValue(stringValue);
        cta.setDoubleValue(doubleValue);
        cta.setIntegerValue(integerValue);

        if(cta.getId() == null ) {
            completedTaskAttributeDao.create(cta);
        } else{
            completedTaskAttributeDao.update(cta);
        }
        //TODO: Log in the log export


    }
}
