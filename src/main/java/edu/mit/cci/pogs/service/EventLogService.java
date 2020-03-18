package edu.mit.cci.pogs.service;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;

@Service
public class EventLogService {

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private SubjectDao subjectDao;

    public JSONObject getLogJson(EventLog el) {
        JSONObject event = new JSONObject();
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
    public void createEventLogFromJsonString(String attributesToAddJson, Long completedTaskId, Long sessionId){
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
                        eventLog.setTimestamp( new Timestamp(jo.getLong("timestamp")));
                    }
                    if (jo.has("eventContent")) {
                        eventLog.setEventContent(jo.getString("eventContent"));
                    }
                    if (jo.has("summaryDescription")) {
                        eventLog.setSummaryDescription(jo.getString("summaryDescription"));
                    }
                    if(jo.has("sender")){
                        Subject su = subjectDao.getByExternalId(jo.getString("sender"));
                        eventLog.setSender(jo.getString("sender"));
                        if(su!=null){
                            eventLog.setSenderSubjectId(su.getId());
                            eventLogDao.create(eventLog);
                        }
                    }
                }
            }
        }
    }
}
