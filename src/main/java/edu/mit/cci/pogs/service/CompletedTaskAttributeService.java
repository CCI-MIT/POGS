package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;

@Service
public class CompletedTaskAttributeService {

    @Autowired
    private CompletedTaskAttributeDao completedTaskAttributeDao;

    public void createOrUpdate(String attributeName, String stringValue, Double doubleValue,
                               Long integerValue, Long completedTaskId,String extraData, boolean mustCreateNewAttribute) {

        CompletedTaskAttribute cta;
        if(mustCreateNewAttribute){
            cta = new CompletedTaskAttribute();
            cta.setAttributeName(attributeName);
            cta.setCompletedTaskId(completedTaskId);
        }
        else {
            cta = completedTaskAttributeDao
                    .getByAttributeNameCompletedTaskId(attributeName, completedTaskId);
            if (cta == null) {
                cta = new CompletedTaskAttribute();
                cta.setAttributeName(attributeName);
                cta.setCompletedTaskId(completedTaskId);
            }
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

    public JSONArray listCompletedTaskAttributesForCompletedTask(Long completedTaskId){
        List<CompletedTaskAttribute> list = completedTaskAttributeDao.listByCompletedTaskId(completedTaskId);
        return attributesToJsonArray(list);
    }

    public JSONArray attributesToJsonArray(List<CompletedTaskAttribute> taskExecutionAttributes) {
        JSONArray configurationArray = new JSONArray();
        for (CompletedTaskAttribute tea : taskExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("completedTaskId", tea.getCompletedTaskId());
            teaJson.put("attributeName", tea.getAttributeName());
            teaJson.put("stringValue", tea.getStringValue());
            teaJson.put("doubleValue", tea.getDoubleValue());
            teaJson.put("integerValue", tea.getIntegerValue());
            teaJson.put("extraData", tea.getExtraData());
            configurationArray.put(teaJson);
        }
        return configurationArray;
    }

    public void createCompletedTaskAttributesFromJsonString(String attributesToAddJson, Long completedTaskId){
        if (attributesToAddJson != null) {

            JSONArray array = new JSONArray(attributesToAddJson);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject jo = array.getJSONObject(i);
                    CompletedTaskAttribute tea = new CompletedTaskAttribute();
                    tea.setAttributeName(jo.getString("attributeName"));
                    tea.setCompletedTaskId(completedTaskId);
                    if (jo.has("stringValue")) {
                        tea.setStringValue(jo.getString("stringValue"));
                    }
                    if (jo.has("integerValue")) {
                        tea.setIntegerValue(jo.getLong("integerValue"));
                    }
                    if (jo.has("doubleValue")) {
                        tea.setDoubleValue(jo.getDouble("doubleValue"));
                    }
                    boolean alreadyExist = false;
                    List<CompletedTaskAttribute> list = completedTaskAttributeDao.listByCompletedTaskId(completedTaskId);
                    for (CompletedTaskAttribute teaz : list) {
                        if (teaz.getAttributeName().equals(tea.getAttributeName())) {
                            teaz.setStringValue(tea.getStringValue());
                            teaz.setIntegerValue(tea.getIntegerValue());
                            teaz.setDoubleValue(tea.getDoubleValue());
                            completedTaskAttributeDao.update(teaz);
                            alreadyExist = true;
                        }
                    }
                    if (!alreadyExist) {
                        completedTaskAttributeDao.create(tea);
                    }
                }
            }
        }
    }
}
