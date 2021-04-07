package edu.mit.cci.pogs.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

import edu.mit.cci.pogs.model.dao.sessionlog.SessionLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;
import edu.mit.cci.pogs.utils.DateUtils;

@Service
public class SessionLogService {

    @Autowired
    private  SessionLogDao sessionLogDao;

    public void createLogFromClient(String externalUserId, String errorMessage, Long sessionId, String url){
        SessionLog sl = new SessionLog();
        sl.setLogTime(new Timestamp(new Date().getTime()));
        sl.setLogType("ERROR");
        sl.setSessionId(sessionId);
        JSONObject jo = new JSONObject();
        jo.put("subjectExternalId", externalUserId);
        jo.put("errorMessage",errorMessage);
        jo.put("url", url);
        sl.setMessage(jo.toString());
        sessionLogDao.create(sl);

    }
    // session starting
    // schedule creation
    // (client) page load
    // (script started)
    // (script ended)
    public void createLogFromSystem(String externalUserId, String action, Long sessionId, String url){
        SessionLog sl = new SessionLog();
        sl.setLogTime(new Timestamp(new Date().getTime()));
        sl.setLogType("SubjectLoaded");
        sl.setSessionId(sessionId);
        JSONObject jo = new JSONObject();
        jo.put("subjectExternalId", externalUserId);
        jo.put("action",action);
        jo.put("url", url);
        sl.setMessage(jo.toString());
        sessionLogDao.create(sl);

    }
    public void createLogFromSystem(Long sessionId, String message){
        SessionLog sl = new SessionLog();
        sl.setLogTime(new Timestamp(new Date().getTime()));
        sl.setLogType("INFO");
        sl.setSessionId(sessionId);
        sl.setMessage(message);
        sessionLogDao.create(sl);

    }
}
