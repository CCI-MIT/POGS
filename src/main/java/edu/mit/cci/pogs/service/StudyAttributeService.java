package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import edu.mit.cci.pogs.model.dao.studyattribute.StudyAttributeDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.StudyAttribute;

@Service
public class StudyAttributeService {

    @Autowired
    private StudyAttributeDao studyAttributeDao;

    public List<StudyAttribute> listStudyAttributes(Long studyId){
        return studyAttributeDao.listByStudyId(studyId);
    }

    public JSONArray listStudyAttributesArray(Long studyId){
        return attributesToJsonArray(listStudyAttributes(studyId));
    }

    public JSONArray attributesToJsonArray(List<StudyAttribute> studyAttributes) {
        JSONArray attributeArray = new JSONArray();
        for (StudyAttribute tea : studyAttributes) {
            JSONObject teaJson = new JSONObject();
            teaJson.put("attributeName", tea.getAttributeName());
            teaJson.put("stringValue", tea.getStringValue());
            teaJson.put("doubleValue", tea.getDoubleValue());
            teaJson.put("integerValue", tea.getIntegerValue());
            teaJson.put("sessionId", tea.getSessionId());
            teaJson.put("studyId", tea.getStudyId());
            attributeArray.put(teaJson);
        }
        return attributeArray;
    }

    public void createOrUpdateStudyAttributes(String attributesToAddJson, Long sessionId, Long studyId) {
        if (attributesToAddJson != null) {

            JSONArray attributes = new JSONArray(attributesToAddJson);

            for (int j = 0; j < attributes.length(); j++) {
                StudyAttribute sa = new StudyAttribute();
                JSONObject att = attributes.getJSONObject(j);
                sa.setAttributeName(att.getString("attributeName"));

                if (att.has("stringValue")) {
                    if (!att.isNull("stringValue")) {
                        sa.setStringValue(att.getString("stringValue"));
                    } else {
                        sa.setStringValue("");
                    }
                }
                if (att.has("integerValue")) {
                    sa.setIntegerValue(new Integer(att.getInt("integerValue")).longValue());
                }
                if (att.has("doubleValue")) {
                    sa.setDoubleValue(att.getDouble("doubleValue"));
                }
                if (att.has("studyId")) {
                    sa.setStudyId(att.getLong("studyId"));
                } else {
                    sa.setStudyId(studyId);
                }
                if (att.has("sessionId")) {
                    sa.setSessionId(att.getLong("sessionId"));
                } else{
                    sa.setSessionId(sessionId);
                }
                boolean alreadyExist = false;
                List<StudyAttribute> list = studyAttributeDao.listBySessionId(sa.getSessionId());
                for (StudyAttribute teaz : list) {
                    if (teaz.getAttributeName().equals(sa.getAttributeName())) {
                        teaz.setStringValue(sa.getStringValue());
                        teaz.setIntegerValue(sa.getIntegerValue());
                        teaz.setDoubleValue(sa.getDoubleValue());
                        studyAttributeDao.update(teaz);
                        alreadyExist = true;
                    }
                }
                if (!alreadyExist) {
                    studyAttributeDao.create(sa);
                }
            }
        }
    }

}
