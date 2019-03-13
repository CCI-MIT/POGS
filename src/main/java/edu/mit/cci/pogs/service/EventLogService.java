package edu.mit.cci.pogs.service;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;

@Service
public class EventLogService {

    @Autowired
    private EventLogDao eventLogDao;

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
}
