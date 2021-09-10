package edu.mit.cci.pogs.view.webhooks;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.dao.sessionlog.SessionLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SessionLog;
import edu.mit.cci.pogs.service.SessionLogService;
import edu.mit.cci.pogs.service.SessionService;
import edu.mit.cci.pogs.utils.EmailUtils;


@RestController
public class JaaSWebhookController {

    @Autowired
    private Environment env;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private SessionLogService sessionLogService;


    @PostMapping("/jitsiwebhook")
    public void process(@RequestBody com.fasterxml.jackson.databind.JsonNode payload) {
        JsonNode eventType = payload.get("eventType");
        JsonNode data = payload.get("data");


        //eventType == TRANSCRIPTION_UPLOADED RECORDING_UPLOADED
        //1. Get session from fqn
        //2. Save JSON to session
        //3. Send email to session's config with sessionURL
        System.out.println(payload);

        if(eventType.asText().equals("RECORDING_UPLOADED")) {
            JsonNode preAuthenticatedLink = data.get("preAuthenticatedLink");

            JsonNode fqn = payload.get("fqn");
            String fqnStr = fqn.asText();
            String videoProviderAPPID = env.getProperty("videoprovider.app_id");
            String prefix = "pogs_session_video_chat_confenrence_76856758976898532342_";
            String sessionAndCompletedTaskIds = fqnStr.replace(videoProviderAPPID+ "/"+prefix,"");
            String[] sessionAndCt = sessionAndCompletedTaskIds.split("_");
            if(sessionAndCt.length>0) {
                Long sessionId = Long.parseLong(sessionAndCt[0]);
                Session session = sessionDao.get(sessionId);
                if(session!=null){
                    EmailUtils.sendEmailToRecipient("POGS session's video chat download link",
                            session.getVideoChatNotificationEmail(), "" +
                                    "Please download this file in the next 24 hours or it will be lost: " +
                                    "<a href='" + preAuthenticatedLink + "'>" + preAuthenticatedLink + "</a>" +
                                    "<br/> POGS admin",
                            env.getProperty("email.smtpHost"),
                            env.getProperty("email.smtpPort"),
                            env.getProperty("email.userName"),
                            env.getProperty("email.password"));
                    //save to session log
                    sessionLogService.createLogFromSystem(session.getId(),
                            "Video chat recording sent to: "+
                                    session.getVideoChatNotificationEmail() +
                                    " with link: " + preAuthenticatedLink);
                }
            }


        }
        if(eventType.asText().equals("TRANSCRIPTION_UPLOADED")) {
            JsonNode preAuthenticatedLink = data.get("preAuthenticatedLink");

            JsonNode fqn = payload.get("fqn");
            String fqnStr = fqn.asText();
            String videoProviderAPPID = env.getProperty("videoprovider.app_id");
            String prefix = "pogs_session_video_chat_confenrence_76856758976898532342_";
            String sessionAndCompletedTaskIds = fqnStr.replace(videoProviderAPPID+ "/"+prefix,"");
            String[] sessionAndCt = sessionAndCompletedTaskIds.split("_");
            if(sessionAndCt.length>0) {
                Long sessionId = Long.parseLong(sessionAndCt[0]);
                Session session = sessionDao.get(sessionId);
                if(session!=null){
                    EmailUtils.sendEmailToRecipient("POGS session's transcription download link",
                            session.getVideoChatNotificationEmail(), "" +
                                    "Please download this file in the next 24 hours or it will be lost: " +
                                    "<a href='" + preAuthenticatedLink + "'>" + preAuthenticatedLink + "</a>" +
                                    "<br/> POGS admin",
                            env.getProperty("email.smtpHost"),
                            env.getProperty("email.smtpPort"),
                            env.getProperty("email.userName"),
                            env.getProperty("email.password"));
                    //save to session log
                    sessionLogService.createLogFromSystem(session.getId(),
                            "Video transcription recording sent to: "+
                                    session.getVideoChatNotificationEmail() +
                                    " with link: " + preAuthenticatedLink);
                }
            }


        }

    }
}
