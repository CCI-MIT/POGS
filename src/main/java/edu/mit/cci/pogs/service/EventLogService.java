package edu.mit.cci.pogs.service;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.messages.CommunicationMessage;
import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Service
public class EventLogService {

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    public JSONObject getLogJson(EventLog el) {
        JSONObject event = new JSONObject();
        event.put("timestamp", el.getTimestamp());
        event.put("sender", el.getSender());
        event.put("receiver", el.getReceiver());

        event.put("content", el.getEventContent());
        event.put("completedTaskId", el.getCompletedTaskId());
        event.put("sessionId", el.getSessionId());
        event.put("type", el.getEventType());
        return event;
    }

    public JSONArray getAllLogsUntilNow(Long completedTaskId) {

        List<EventLog> allLogsUntilNow = eventLogDao.listLogsUntil(completedTaskId, new Date());
        JSONArray allLogs = new JSONArray();
        for (EventLog el : allLogsUntilNow) {
            allLogs.add(getLogJson(el));
        }

        return allLogs;
    }

    public void createEventLogFromJsonString(String attributesToAddJson, Long completedTaskId, Long sessionId) {
        if (attributesToAddJson != null) {

            org.json.JSONArray array = new org.json.JSONArray(attributesToAddJson);
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject jo = array.getJSONObject(i);
                    EventLog eventLog = new EventLog();
                    eventLog.setSessionId(sessionId);
                    eventLog.setCompletedTaskId(completedTaskId);
                    if (jo.has("eventType")) {
                        eventLog.setEventType(jo.getString("eventType"));
                    }
                    if (jo.has("timestamp")) {
                        eventLog.setTimestamp(new Timestamp(jo.getLong("timestamp")));
                    }
                    if (jo.has("eventContent")) {
                        eventLog.setEventContent(jo.getString("eventContent"));
                    }
                    if (jo.has("summaryDescription")) {
                        eventLog.setSummaryDescription(jo.getString("summaryDescription"));
                    }
                    if (jo.has("sender")) {
                        Subject su = subjectDao.getByExternalId(jo.getString("sender"));
                        eventLog.setSender(jo.getString("sender"));
                        if (su != null) {
                            eventLog.setSenderSubjectId(su.getId());
                            eventLogDao.create(eventLog);
                        }
                    }
                }
            }
        }
    }

    public String getScriptForLogs(Long sessionId) {
        List<EventLog> eventLogs = eventLogDao.listLogsBySessionId(sessionId);
        String scriptData = "// Script for session id: " + sessionId + "\n";
        scriptData += "// Events ordered by timestamp \n";
        scriptData += "var sessionEventArray={};\n";
        Map<String, String> subjects = new HashMap<>();
        Map<Long, CompletedTask> completedTaskMap = new HashMap<>();
        Map<Long, Long> taskMap = new HashMap<>();
        eventLogs.stream().forEach(el -> subjects.put(el.getSender(), el.getSender()));
        eventLogs.stream().forEach(el -> {
                    if (el.getCompletedTaskId() != null)
                        completedTaskMap.putIfAbsent(
                                el.getCompletedTaskId(),
                                completedTaskDao.get(el.getCompletedTaskId()));
                }
        );
        completedTaskMap.values().stream().forEach(ct ->taskMap.putIfAbsent(ct.getTaskId(),ct.getTaskId()) );
        for (Long ct : taskMap.keySet()) {
            scriptData += "sessionEventArray[\"" + (ct) + "\"] = [];\n";
        }

        Long lastTaskId = null;
        for (EventLog eventLog : eventLogs) {
            if (eventLog.getCompletedTaskId() != null) {

                if (!eventLog.getEventType().equals(CommunicationMessage.CommunicationType.CHECK_IN.name())) {
                    CompletedTask ct = completedTaskMap.get(eventLog.getCompletedTaskId());
                    Long startTime = ct.getStartTime().getTime();
                    Long taskId = ct.getTaskId();
                    if (lastTaskId == null ||
                            (lastTaskId.longValue() != taskId.longValue())) {
                        scriptData += "// Task id " + taskId + "\n";
                        lastTaskId = taskId;
                    }

                    JSONObject jo = getLogJson(eventLog);
                    scriptData += "sessionEventArray[\"" + taskId + "\"].push({\"timestamp\":" + (eventLog.getTimestamp().getTime() - startTime) + ", ";
                    scriptData += "\"rawEventData\":" + jo.toString() + "});\n";

                }
            }
        }
        scriptData += "//This next line must remain in the file\n";
        scriptData +="sessionEvents= JSON.stringify(sessionEventArray)";

        return scriptData;

    }

    //Loop the events and group by TASK.
    //order by timestamp.
    //send them at the right timestamp?

}
