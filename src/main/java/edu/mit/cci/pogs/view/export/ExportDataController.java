package edu.mit.cci.pogs.view.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.completedtaskattribute.CompletedTaskAttributeDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;

import static edu.mit.cci.pogs.constants.ApplicationConstants.SESSION;


public class ExportDataController {

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    @Autowired
    private CompletedTaskAttributeDao completedTaskAttributeDao;

    @Autowired
    private SessionDao sessionDao;

    //@GetMapping("/admin/export/session/{sessionId}")
    public void exportSessionData(HttpServletResponse response, @PathVariable("sessionId") Long sessionId) {
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=Session Report " + new Date().toString() + ".zip");

            List<ExportFile> ex = new ArrayList<>();
            List<EventLog> eventLogList = eventLogDao.listLogsBySessionId(sessionId);
            Map<Long,Long> completedTaskIdToSessionId = new HashMap<>();

            for(EventLog el: eventLogList){
                completedTaskIdToSessionId.put(el.getCompletedTaskId(), el.getSessionId());
            }

            List<Long> completedTaskIds = (List<Long>) completedTaskIdToSessionId.keySet();

            List<CompletedTask> completedTaskList = completedTaskDao.listByCompletedTaskIds(completedTaskIds);
            ex.add(getEntityDataExportFile(EventLog.class, eventLogList, null, sessionId,
                    false, null));

            List<CompletedTaskScore> completedTaskScoreList =
                    completedTaskScoreDao.listByCompletedTasksIds(completedTaskIds);

            ex.add(getEntityDataExportFile(CompletedTaskScore.class, completedTaskScoreList,
                    null, sessionId,
                    false, completedTaskIdToSessionId));


            List<CompletedTaskAttribute> completedTaskAttributesList =
                    completedTaskAttributeDao.listByCompletedTasksIds(completedTaskIds);

            ex.add(getEntityDataExportFile(CompletedTaskAttribute.class, completedTaskAttributesList,
                    null, sessionId,
                    false, completedTaskIdToSessionId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private StringBuffer getHeaderForEntity(Method[] methods, boolean isStudyExport){

        Map<String,String> headers = new LinkedHashMap<>();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                String aux = method.getName().replaceFirst("get", "");
                headers.put(aux,aux);
            }
        }
        if(isStudyExport){
            headers.put("SessionId", "SessionId");
        }

        StringBuffer header = new StringBuffer();
        for(String s: headers.values()){
            header.append(s + ",");
        }

        return header;
    }
    private <T> ExportFile getEntityDataExportFile(Class<T> clazz, List<T> list, Long studyId, Long sessionId,
                                         boolean isStudyExport, Map<Long,Long> completedTaskIdsToSessionsMap) {
        ExportFile ef = new ExportFile();
        if (studyId != null) {
            ef.fileName = clazz.getSimpleName() + "_study_" + String.valueOf(studyId) + "_" + LocalDateTime.now() + ".csv";
        } else {
            ef.fileName = clazz.getSimpleName() + "_session_" + String.valueOf(sessionId) + "_" + LocalDateTime.now() + ".csv";
        }

        Method[] methods = clazz.getDeclaredMethods();
        StringBuffer header = getHeaderForEntity(methods,isStudyExport);

        StringBuffer content = new StringBuffer();

        for (T element : list) {
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    try {
                        content.append(method.invoke(element, null));
                        content.append(",");

                    } catch (InvocationTargetException ite) {
                        ite.printStackTrace();
                    } catch (IllegalAccessException iae){
                        iae.printStackTrace();
                    }
                }
            }


            if (list.size() > 0 && list.get(0) instanceof CompletedTaskScore) {
                CompletedTaskScore o = (CompletedTaskScore) list.get(0);
                content.append(String.valueOf(completedTaskIdsToSessionsMap.get(o.getCompletedTaskId())));
            }
            if (list.size() > 0 && list.get(0) instanceof CompletedTask) {
                CompletedTask o = (CompletedTask) list.get(0);
                content.append(String.valueOf(completedTaskIdsToSessionsMap.get(o.getId())));
            }
            if (list.size() > 0 && list.get(0) instanceof CompletedTaskAttribute) {
                CompletedTaskAttribute o = (CompletedTaskAttribute) list.get(0);
                content.append(String.valueOf(completedTaskIdsToSessionsMap.get(o.getCompletedTaskId())));
            }

            content.append("\n");
        }

        header.append(content);
        ef.fileContent = header.toString();
        return ef;

    }

    //get session data zip
    // - get event logs (file)
    // - get completed task attributes (file)
    // - get completed task
    // - get completed task scores

    class ExportFile {

        String fileName;
        String fileContent;

        public void writeContents() {
            PrintWriter writer1 = null;
            try {

                writer1 = new PrintWriter(new OutputStreamWriter
                        (new FileOutputStream(fileName), "UTF-8"));
                writer1.print(fileContent);
                writer1.close();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
