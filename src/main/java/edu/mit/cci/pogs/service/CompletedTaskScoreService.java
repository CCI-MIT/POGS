package edu.mit.cci.pogs.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;

@Service
public class CompletedTaskScoreService {

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    public void createCompletedTaskScoreFromScript(String compledTaskScoreJSONObject, Long completedTaskId) {
        if (compledTaskScoreJSONObject != null) {

            JSONObject object = new JSONObject(compledTaskScoreJSONObject);

            CompletedTaskScore cts = completedTaskScoreDao.getByCompletedTaskId(completedTaskId);

            boolean shouldCreate = false;
            if(cts==null){
                cts = new CompletedTaskScore();
                shouldCreate = true;
            }

            if (object.has("totalScore")) {


                cts.setTotalScore(object.getDouble("totalScore"));

                if (object.has("numberOfRightAnswers")) {

                    cts.setNumberOfRightAnswers(object.getInt("numberOfRightAnswers"));
                }

                if (object.has("numberOfWrongAnswers")) {
                    cts.setNumberOfWrongAnswers(object.getInt("numberOfWrongAnswers"));
                }

                if (object.has("numberOfEntries")) {
                    cts.setNumberOfEntries(object.getInt("numberOfEntries"));
                }

                if (object.has("numberOfProcessedEntries")) {
                    cts.setNumberOfProcessedEntries(object.getInt("numberOfProcessedEntries"));
                }

                if (object.has("scoringData")) {
                    cts.setScoringData(object.getString("scoringData"));
                }
                cts.setCompletedTaskId(completedTaskId);
                if(shouldCreate) {
                    completedTaskScoreDao.create(cts);
                } else {

                    completedTaskScoreDao.update(cts);
                }
            }
        }
    }

    public List<CompletedTaskScore> listCompletedTaskScore(List<CompletedTask> allCompletedTasks){
        List<Long> allCompletedTaskIds = new ArrayList<>();
        allCompletedTasks.stream().forEach(a -> allCompletedTaskIds.add(a.getId()));
        return this.completedTaskScoreDao.listByCompletedTasksIds(allCompletedTaskIds);
    }
}
