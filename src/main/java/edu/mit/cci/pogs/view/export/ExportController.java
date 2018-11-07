package edu.mit.cci.pogs.view.export;

import edu.mit.cci.pogs.model.dao.completedtask.CompletedTaskDao;
import edu.mit.cci.pogs.model.dao.eventlog.EventLogDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class ExportController {

    @Autowired
    private EventLogDao eventLogDao;

    @Autowired
    private CompletedTaskDao completedTaskDao;

    @GetMapping("/admin/export/session/eventlog/{sessionId}")
    public void exportSessionEventLog(HttpServletResponse response, @PathVariable("sessionId") Long sessionId)
                            throws IOException, IllegalAccessException{
        List<EventLog> eventLogList = eventLogDao.listLogsBySessionId(sessionId);
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename="
                + "SessionEventLog_" + String.valueOf(sessionId)+ "_" + LocalDateTime.now() + ".csv");

        try {
            Field[] attributes =  EventLog.class.getDeclaredFields();
            writeDataToCSV(attributes,eventLogList,response);
        } catch (Exception e) {
            throw e;
        }
    }

    private <T> void writeDataToCSV(Field[] attributes,List<T> list, HttpServletResponse response ) throws IOException,IllegalAccessException{
        OutputStream out = response.getOutputStream();
        // Write the header line
        StringBuilder header = new StringBuilder();
        for (Field field:attributes){
            header.append(field.getName());
            header.append(",");
        }
        header.append("\n");
        out.write(header.toString().getBytes());
        // Write the content
        StringBuilder line = new StringBuilder();
        for (T element : list){
            for (Field field:attributes){
                line.append(field.get(element));
                line.append(",");
            }
            line.append("\n");
        }
        out.write(line.toString().getBytes());
        out.flush();
    }
}
