package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.sessionexecutionattribute.SessionExecutionAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;

@Service
public class SessionExecutionAttributeService {

    @Autowired
    private SessionExecutionAttributeDao sessionExecutionAttributeDao;

    public JSONArray listSessionExecutionAttributesAsJsonArray(Long sessionId){

        List<SessionExecutionAttribute> taskExecutionAttributes = sessionExecutionAttributeDao
                .listBySessionId(sessionId);
        return attributesToJsonArray(taskExecutionAttributes);
    }

    public JSONArray attributesToJsonArray(List<SessionExecutionAttribute> sessionExecutionAttributes) {
        JSONArray configurationArray = new JSONArray();
        for (SessionExecutionAttribute tea : sessionExecutionAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("attributeName", tea.getAttributeName());
            teaJson.put("stringValue", tea.getStringValue());
            teaJson.put("doubleValue", tea.getDoubleValue());
            teaJson.put("integerValue", tea.getIntegerValue());
            configurationArray.put(teaJson);


        }
        return configurationArray;
    }

    public void createSessionExecutionAttributesFromJsonString(String attributesToAddJson, Long sessionId){
        if (attributesToAddJson != null) {

            JSONArray array = new JSONArray(attributesToAddJson);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject jo = array.getJSONObject(i);
                    SessionExecutionAttribute tea = new SessionExecutionAttribute();
                    tea.setAttributeName(jo.getString("attributeName"));
                    tea.setSessionId(sessionId);
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
                    List<SessionExecutionAttribute> list = sessionExecutionAttributeDao.listBySessionId(sessionId);
                    for (SessionExecutionAttribute teaz : list) {
                        if (teaz.getAttributeName().equals(tea.getAttributeName())) {
                            teaz.setStringValue(tea.getStringValue());
                            teaz.setIntegerValue(tea.getIntegerValue());
                            teaz.setDoubleValue(tea.getDoubleValue());
                            sessionExecutionAttributeDao.update(teaz);
                            alreadyExist = true;
                        }
                    }
                    if (!alreadyExist) {
                        sessionExecutionAttributeDao.create(tea);
                    }
                }
            }
        }
    }

    public List<SessionExecutionAttribute> listAttributes(Long id) {
        return sessionExecutionAttributeDao.listBySessionId(id);
    }
}
