package edu.mit.cci.pogs.service;


import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.dao.dictionary.DictionaryDao;
import edu.mit.cci.pogs.model.dao.dictionaryentry.DictionaryEntryDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskplugin.AnswerKeyFormat;
import edu.mit.cci.pogs.model.dao.taskplugin.ScoringType;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPluginProperties;
import edu.mit.cci.pogs.model.dao.unprocesseddictionaryentry.UnprocessedDictionaryEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Dictionary;
import edu.mit.cci.pogs.model.jooq.tables.pojos.DictionaryEntry;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.UnprocessedDictionaryEntry;
import edu.mit.cci.pogs.runner.wrappers.TaskWrapper;

@Service
public class CompletedTaskService {

    @Autowired
    private CompletedTaskAttributeDao completedTaskAttributeDao;

    @Autowired
    private TaskExecutionAttributeDao taskExecutionAttributeDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    @Autowired
    private UnprocessedDictionaryEntryDao unprocessedDictionaryEntryDao;

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private DictionaryDao dictionaryDao;

    @Autowired
    protected CompletedTaskAttributeService completedTaskAttributeService;

    @Autowired
    protected TaskExecutionAttributeService taskExecutionAttributeService;

    public void scoreCompletedTask(CompletedTask ct, TaskWrapper tw) {

        TaskPlugin pl = TaskPlugin.getTaskPlugin(tw.getTaskPluginType());
        TaskHasTaskConfiguration thtc = taskHasTaskConfigurationDao.getByTaskId(tw.getId());

        if (pl == null) {
            return;
        }
        TaskPluginProperties tpp = pl.getTaskPluginProperties();
        if (tpp.getScoring() == null) {
            return;
        }


        if (tpp.getScoring().getScoringType().getId().equals(ScoringType.externalService.getId())) {

            getScoreFromExternalService(tpp,tw, ct, thtc);
        }
    }

    private void getScoreFromExternalService(TaskPluginProperties tp , TaskWrapper task,
                                               CompletedTask ct, TaskHasTaskConfiguration thtc) {
        Double score = new Double(0);
        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(tp.getScoring().getUrl());

            List<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("pluginName", tp.getName()));
            params.add(new BasicNameValuePair("taskId", task.getId().toString()));
            params.add(new BasicNameValuePair("completedTaskId", ct.getId().toString()));

            params.add(new BasicNameValuePair("taskExecutionAttibutes", taskExecutionAttributeService.listExecutionAttributesAsJsonArray(
                    task.getId()).toString()));

            params.add(new BasicNameValuePair("completedTaskAttributes", (completedTaskAttributeService
                    .listCompletedTaskAttributesForCompletedTask(ct.getId())).toString()));

            TaskConfiguration tc = taskConfigurationDao.get(thtc.getTaskConfigurationId());

            if(tc.getDictionaryId()!= null ){

                Dictionary dict = dictionaryDao.get(tc.getDictionaryId());
                params.add(new BasicNameValuePair("dictionaryEntries", (dictionaryService
                        .listDictionaryEntriesJson(tc.getDictionaryId())).toString()));
                params.add(new BasicNameValuePair("dictionaryId",tc.getDictionaryId().toString()));
                params.add(new BasicNameValuePair("dictionaryHasGroundTruth",
                        dict.getHasGroundTruth() + ""));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            CloseableHttpResponse response = client.execute(httpPost);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            JSONObject jo = new JSONObject(result.toString());
            if(jo.has("completedTaskScore")) {
                CompletedTaskScore cts;
                cts = completedTaskScoreDao.getByCompletedTaskId(ct.getId());

                if (cts == null) {
                    cts = new CompletedTaskScore();
                    cts.setCompletedTaskId(ct.getId());
                }



                JSONObject completedTaskScore = jo.getJSONObject("completedTaskScore");

                if(completedTaskScore.has("numberOfProcessedEntries")) {
                    cts.setNumberOfProcessedEntries(completedTaskScore.getInt("numberOfProcessedEntries"));
                }
                if(completedTaskScore.has("numberOfWrongAnswers")) {
                    cts.setNumberOfWrongAnswers(completedTaskScore.getInt("numberOfWrongAnswers"));
                }
                if(completedTaskScore.has("numberOfRightAnswers")) {
                    cts.setNumberOfRightAnswers(completedTaskScore.getInt("numberOfRightAnswers"));
                }
                if(completedTaskScore.has("scoringData")) {
                    cts.setScoringData(completedTaskScore.getString("scoringData"));
                }

                cts.setCompletedTaskId(ct.getId());

                if(completedTaskScore.has("numberOfEntries")) {
                    cts.setNumberOfEntries(completedTaskScore.getInt("numberOfEntries"));
                }
                if(completedTaskScore.has("totalScore")) {
                    cts.setTotalScore(completedTaskScore.getDouble("totalScore"));
                }

                if (cts.getId() == null) {
                    completedTaskScoreDao.create(cts);
                } else {
                    completedTaskScoreDao.update(cts);
                }

            }
            if(jo.has("unprocessedEntries")) {
                //create object in the database
                // originalText predictedCategory // dictionary ID.
                JSONArray completedTaskScore = jo.getJSONArray("unprocessedEntries");
                for(int i = 0 ; i < completedTaskScore.length(); i ++) {
                    UnprocessedDictionaryEntry ude = new UnprocessedDictionaryEntry();
                    JSONObject jsonObject = completedTaskScore.getJSONObject(i);
                    if(jsonObject.has("dictionaryId")) {
                        ude.setDictionaryId(jsonObject.getLong("dictionaryId"));
                    } else {
                        ude.setDictionaryId(tc.getDictionaryId());
                    }
                    if(jsonObject.has("entryPredictedCategory")) {
                        ude.setEntryPredictedCategory(jsonObject.getString("entryPredictedCategory"));
                    }
                    if(jsonObject.has("entryValue")) {
                        ude.setEntryValue(jsonObject.getString("entryValue"));
                    }
                    ude.setHasBeenProcessed(false);
                    unprocessedDictionaryEntryDao.create(ude);
                }

            }

            client.close();
        } catch (IOException exeption) {
            exeption.printStackTrace();
        }

    }

}
