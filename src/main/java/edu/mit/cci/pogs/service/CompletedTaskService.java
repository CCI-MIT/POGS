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
import edu.mit.cci.pogs.model.dao.taskexecutionattribute.TaskExecutionAttributeDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskplugin.AnswerKeyFormat;
import edu.mit.cci.pogs.model.dao.taskplugin.ScoringType;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPluginProperties;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
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
        List<TaskExecutionAttribute> teas = taskExecutionAttributeDao
                .listByTaskConfigurationId(thtc.getTaskConfigurationId());
        if (tpp.getScoring().getScoringType().equals(ScoringType.indexedAnswerOneAnswerKey)) {


            TaskExecutionAttribute answerKey = null;
            for (TaskExecutionAttribute tea : teas) {
                if (tea.getAttributeName().contains(tpp.getScoring().getAnswerKeyPrefix())) {
                    answerKey = tea;
                }
            }
            if (answerKey == null) {
                return;
            }
            Double score = new Double(0);
            List<CompletedTaskAttribute> completedTaskAttributeList = completedTaskAttributeDao
                    .listByAttributePrefix(tpp.getScoring().getAnswerSheetPrefix(),
                            ct.getId());
            for (int i = 0; i < completedTaskAttributeList.size(); i++) {
                CompletedTaskAttribute cta = completedTaskAttributeList.get(i);
                String index = cta.getAttributeName().replace(
                        tpp.getScoring().getAnswerSheetPrefix(), "");
                int indexInt = Integer.parseInt(index);
                if (tpp.getScoring().getAnswerKeyFormat().equals(AnswerKeyFormat.CSV)) {
                    String[] answers = answerKey.getStringValue().split(",");
                    if (answers[indexInt].equals(cta.getStringValue())) {
                        score = score + 1;
                    } else {
                        score = score;
                    }
                } else {
                    if (tpp.getScoring().getAnswerKeyFormat().equals(AnswerKeyFormat.JSONArray)) {
                        JSONArray ja = new JSONArray(cta.getStringValue());
                        String value = ja.getString(indexInt);
                        if (value.equals(cta.getStringValue())) {
                            score = score + 1;
                        } else {
                            score = score;
                        }
                    }
                }
            }
            saveOrUpdateCompletedTaskScore(ct, score);
        }
        if (tpp.getScoring().getScoringType().equals(ScoringType.externalService)) {
            List<CompletedTaskAttribute> completedTaskAttributeList = completedTaskAttributeDao
                    .listByAttributePrefix(tpp.getScoring().getAnswerSheetPrefix(),
                            ct.getId());


            Double score = getScoreFromExternalService(tpp,teas,completedTaskAttributeList);
            if (score != null) {
                saveOrUpdateCompletedTaskScore(ct, score);
            }
        }
        if (tpp.getScoring().getScoringType().equals(ScoringType.scoreIsAttribute)) {
            CompletedTaskAttribute cta = completedTaskAttributeDao.
                    getByAttributeNameCompletedTaskId(tpp.getScoring().getScoreAttributeName(), ct.getId());
            if (cta != null) {
                Double score = Double.parseDouble(cta.getStringValue());
                if (score != null) {
                    saveOrUpdateCompletedTaskScore(ct, score);
                }
            }
        }
    }

    private Double getScoreFromExternalService(TaskPluginProperties tp ,
                                               List<TaskExecutionAttribute> teas ,
                                               List<CompletedTaskAttribute> attributes) {
        Double score = new Double(0);
        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(tp.getScoring().getUrl());

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("pluginName", tp.getName()));
            for(TaskExecutionAttribute tea: teas){
                params.add(new BasicNameValuePair(tea.getAttributeName(), tea.getStringValue()));
            }
            for(CompletedTaskAttribute cta : attributes) {
                params.add(new BasicNameValuePair(cta.getAttributeName(), cta.getStringValue()));
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
            JSONObject jo = new JSONObject(result);
            if(jo.has("score")) {
                String scoreStr = jo.getString("score");
                score = new Double(scoreStr);
            }

            client.close();
        } catch (IOException exeption) {

        }
        return score;
    }

    private void saveOrUpdateCompletedTaskScore(CompletedTask ct, Double score) {
        CompletedTaskScore cts;
        cts = completedTaskScoreDao.getByCompletedTaskId(ct.getId());

        if (cts == null) {
            cts = new CompletedTaskScore();
            cts.setCompletedTaskId(ct.getId());
        }
        cts.setTotalScore(new Double(score));
        if (cts.getId() == null) {
            completedTaskScoreDao.create(cts);
        } else {
            completedTaskScoreDao.update(cts);
        }
    }
}
