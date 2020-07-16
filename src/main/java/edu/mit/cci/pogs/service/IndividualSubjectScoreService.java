package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.mit.cci.pogs.model.dao.individualsubjectscore.IndividualSubjectScoreDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Service
public class IndividualSubjectScoreService {

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private IndividualSubjectScoreDao individualSubjectScoreDao;

    public void createIndividualSubjectScoreFromJsonObject(JSONObject jsonObject, Long completedTaskId) {

        if (jsonObject.has("author") && !jsonObject.get("author").equals("")) {

            String subjectExternalId = jsonObject.getString("author");
            Subject su = subjectDao.getByExternalId(subjectExternalId);
            if (su != null) {
                IndividualSubjectScore iss = individualSubjectScoreDao
                        .getByGiven(su.getId(), completedTaskId);
                boolean shouldCreateNew = false;
                if (iss == null) {
                    iss = new IndividualSubjectScore();
                    shouldCreateNew = true;
                }

                iss.setSubjectId(su.getId());
                iss.setCompletedTaskId(completedTaskId);

                Double score = jsonObject.getDouble("score");
                iss.setIndividualScore(score);
                Double max_score = 0.0;
                JSONObject extraData = new JSONObject();
                if (jsonObject.has("max_score")) {
                    max_score = jsonObject.getDouble("max_score");
                    extraData.put("maxGroundTruthScore", max_score);
                } else {
                    if (jsonObject.has("ground_text_score")) {
                        max_score = jsonObject.getDouble("ground_text_score");
                        extraData.put("maxGroundTruthScore", max_score);
                    }
                    if (jsonObject.has("color")) {
                        extraData.put("color", jsonObject.getString("color"));
                    }
                }
                if (!extraData.toString().equals("")) {
                    iss.setExtraData(extraData.toString());
                }
                if (shouldCreateNew) {
                    individualSubjectScoreDao.create(iss);
                } else {
                    individualSubjectScoreDao.update(iss);
                }

            }
        }
    }

    public void createIndividualSubjectScoreFromScript(String individualTaskScoreJSONObject,
                                                       Long completedTaskId) {
        if (individualTaskScoreJSONObject != null) {

            JSONArray array = new JSONArray(individualTaskScoreJSONObject);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {

                    JSONObject object = array.getJSONObject(i);
                    String subjectExternalId = "";
                    if (object.has("subjectExternalId")) {
                        subjectExternalId = object.getString("subjectExternalId");
                        Subject su = subjectDao.getByExternalId(subjectExternalId);
                        if (su != null) {
                            IndividualSubjectScore iss = individualSubjectScoreDao
                                    .getByGiven(su.getId(), completedTaskId);
                            boolean shouldCreateNew = false;
                            if (iss == null) {
                                iss = new IndividualSubjectScore();
                                iss.setSubjectId(su.getId());
                                iss.setCompletedTaskId(completedTaskId);
                                shouldCreateNew = true;
                            }
                            iss.setIndividualScore(object.getDouble("individualScore"));
                            if (object.has("scoringData")) {
                                iss.setExtraData(object.getString("scoringData"));
                            }
                            if (shouldCreateNew) {
                                individualSubjectScoreDao.create(iss);
                            } else {
                                individualSubjectScoreDao.update(iss);
                            }
                        }
                    }
                }
            }
        }
    }
}
