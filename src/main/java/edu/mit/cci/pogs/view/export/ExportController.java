package edu.mit.cci.pogs.view.export;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.completedtaskscore.CompletedTaskScoreDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static edu.mit.cci.pogs.constants.ApplicationConstants.SESSION;
import static edu.mit.cci.pogs.constants.ApplicationConstants.SESSION_ID;
import static edu.mit.cci.pogs.constants.ApplicationConstants.STUDY;

@RestController
public class ExportController {

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @Autowired
    private CompletedTaskScoreDao completedTaskScoreDao;

    @Autowired
    private SessionDao sessionDao;

    @GetMapping("/admin/export/session/{sessionId}")
    public void exportSessionData(HttpServletResponse response, @PathVariable("sessionId") Long sessionId){
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=Session Report " + new Date().toString()  + ".zip");

//            Event Log Data for the Session
            List<EventLog> eventLogList = eventLogDao.listLogsBySessionId(sessionId);
            if (eventLogList==null)
                throw new Exception("Event Log Data Reading Failed");
            getAllData(response,sessionId,eventLogList,SESSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/admin/export/study/{studyId}")
    public void getStudyData(HttpServletResponse response, @PathVariable("studyId") Long studyId){
        List<Session> sessionInStudy = sessionDao.listByStudyId(studyId);
        try{
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=Study Report " + new Date().toString()  + ".zip");
            List<EventLog> eventLogList = new ArrayList<>();
            for (Session session:sessionInStudy)
                eventLogList.addAll(eventLogDao.listLogsBySessionId(session.getId()));
            getAllData(response,studyId,eventLogList,STUDY);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private <T> String writeDataToCSV(Field[] attributes,List<T> list, Map<Long,Long>... taskSessionMap)
            throws IllegalAccessException{
        boolean isMapPresent = false;
        if (taskSessionMap.length>0  && taskSessionMap[0].size()>0)
            isMapPresent = true;
        int i = 0;
        if (isMapPresent && list.get(0) instanceof CompletedTaskScore)
            i=2;
        StringBuilder header = new StringBuilder();
        for (Field field:attributes){
            header.append(field.getName());
            header.append(",");
        }
        if (isMapPresent)
            header.append(SESSION_ID);
        header.append("\n");
        StringBuilder line = new StringBuilder();
        for (T element : list){
            for (Field field:attributes){
                line.append(field.get(element));
                line.append(",");
            }
            if (isMapPresent)
                line.append(String.valueOf(taskSessionMap[0].get(attributes[i].get(element))));
            line.append("\n");
        }
        header.append(line);
        return String.valueOf(header);
    }

    private void filesToZip(HttpServletResponse response, File... files) throws IOException {
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        // create the ZIP file
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        // compress the files
        for(int i=0; i<files.length; i++) {
            FileInputStream in = new FileInputStream(files[i].getName());
            // add ZIP entry to output stream
            out.putNextEntry(new ZipEntry(files[i].getName()));
            // transfer bytes from the file to the ZIP file
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // complete the entry
            out.closeEntry();
            in.close();
        }
        // complete the ZIP file
        out.close();
    }

    private void getAllData(HttpServletResponse response, Long id, List<EventLog> eventLogList, String type){
        try{
            String eventLogFileName = type+"EventLog_" + String.valueOf(id)+ "_" + LocalDateTime.now() + ".csv";
            eventLogFileName=eventLogFileName.replaceAll(":","_");
            PrintWriter writer1 = new PrintWriter(new OutputStreamWriter
                    (new FileOutputStream(eventLogFileName), "UTF-8"));
            writer1.print(writeDataToCSV(EventLog.class.getDeclaredFields(),eventLogList));
            writer1.close();

//            Get Completed Task Ids
            List<Long> completedTaskIds = new ArrayList<>();
            for (EventLog eventLog:eventLogList)
                if (!completedTaskIds.contains(eventLog.getCompletedTaskId()))
                    completedTaskIds.add(eventLog.getCompletedTaskId());
//            Build a map of completed task id and session id for study
            Map<Long, Long> taskSessionMap = new HashMap<>();
            if (type=="Study"){
                  for (EventLog eventLog:eventLogList)
                      taskSessionMap.put(eventLog.getCompletedTaskId(),eventLog.getSessionId());
            }

//            Completed Task Data for the Session
            List<CompletedTask> completedTaskList = completedTaskDao.listByCompletedTaskIds(completedTaskIds);
            if (completedTaskList==null)
                throw new Exception("Completed Tasks Data Reading Failed");

            String completedTaskFileName = type+"CompletedTasks_" + String.valueOf(id)+ "_" + LocalDateTime.now() + ".csv";
            completedTaskFileName=completedTaskFileName.replaceAll(":","_");
            PrintWriter writer2 = new PrintWriter(new OutputStreamWriter
                    (new FileOutputStream(completedTaskFileName), "UTF-8"));
            writer2.print(writeDataToCSV(CompletedTask.class.getDeclaredFields(),completedTaskList,taskSessionMap));
            writer2.close();

//            Get Completed Task Scores
            List<CompletedTaskScore> completedTaskScoreList = completedTaskScoreDao.listByCompletedTasksIds(completedTaskIds);
            if (completedTaskScoreList==null)
                throw new Exception("Completed Task Scores Data Reading Failed");

            String completedTaskScoreFileName = type+"CompletedTaskScores_" + String.valueOf(id)+ "_" + LocalDateTime.now() + ".csv";
            completedTaskScoreFileName=completedTaskScoreFileName.replaceAll(":","_");
            PrintWriter writer3 = new PrintWriter(new OutputStreamWriter
                    (new FileOutputStream(completedTaskScoreFileName), "UTF-8"));
            writer3.print(writeDataToCSV(CompletedTaskScore.class.getDeclaredFields(),completedTaskScoreList,taskSessionMap));
            writer3.close();

//            Compute the Zip File
            File file1 = new File(eventLogFileName);
            File file2 = new File(completedTaskFileName);
            File file3 = new File(completedTaskScoreFileName);
            filesToZip(response, file1,file2,file3);
            file1.delete();
            file2.delete();
            file3.delete();
            response.flushBuffer();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}