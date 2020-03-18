package edu.mit.cci.pogs.view.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.service.summaryexport.SummaryExportService;
import edu.mit.cci.pogs.utils.ExportUtils;

@RestController
public class SummaryController {

    @Autowired
    private Environment env;

    @Autowired
    private SummaryExportService summaryExportService;


    @GetMapping("/admin/export/summary/tasks/study/{studyId}")
    public void exportSessionTaskSummaryForStudy(HttpServletRequest request, HttpServletResponse response,
                                           @PathVariable("studyId") Long studyId) {
        generateTaskSummary(request,response,null,studyId);
    }
    @GetMapping("/admin/export/summary/tasks/session/{sessionId}")
    public void exportSessionTaskSummaryForSessions(HttpServletRequest request, HttpServletResponse response,
                                             @PathVariable("sessionId") Long sessionId) {
        generateTaskSummary(request,response,sessionId,null);
    }
    private void generateTaskSummary(HttpServletRequest request, HttpServletResponse response,
                                         Long sessionId, Long studyId){
        ExportFile ef = summaryExportService.exportTaskScoreSummaryFiles(studyId,
                sessionId, getPath(request));
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=TaskScore_"+
                            ((studyId!=null)?("study["+studyId+"]"):("session["+sessionId+"]"))+"__" +
                            ExportUtils.getTimeFormattedNoSpaces(new
                                    Timestamp(new Date().getTime())) + ".zip");



            List<ExportFile> sessionExportFiles = new ArrayList<>();

            sessionExportFiles.add(ef);
            filesToZip(response, sessionExportFiles);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/admin/export/summary/eventlog/study/{studyId}")
    public void exportEventLogDataForStudy(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("studyId") Long studyId) {
        generateEventLogSummary(request,response, null, studyId);
    }
    @GetMapping("/admin/export/summary/eventlog/session/{sessionId}")
    public void exportEventLogDataForSession(HttpServletRequest request, HttpServletResponse response,
                                  @PathVariable("sessionId") Long sessionId) {
        generateEventLogSummary(request,response, sessionId, null);
    }
    private void generateEventLogSummary(HttpServletRequest request, HttpServletResponse response,
                                          Long sessionId, Long studyId){

        ExportFile ef = summaryExportService.exportEventLog(studyId,sessionId, getPath(request));

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Event_Log_Session_"+
                            ((studyId!=null)?("study["+studyId+"]"):("session["+sessionId+"]"))
                            +"__" + ExportUtils.getTimeFormattedNoSpaces(
                                    new Timestamp(new Date().getTime())) + ".zip");



            List<ExportFile> sessionExportFiles = new ArrayList<>();

            sessionExportFiles.add(ef);
            filesToZip(response, sessionExportFiles);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private String getPath(HttpServletRequest request){
        String path = env.getProperty("images.dir");
        if (path == null) {
            path = request.getSession().getServletContext().getRealPath("/");
        }
        return path + "/";
    }

    private void filesToZip(HttpServletResponse response, List<ExportFile> files) throws IOException {
        Map<String,ExportFile> uniqueFiles = new HashMap<>();
        for(ExportFile ef:files){
            ef.writeContents();
            uniqueFiles.put(ef.getFileName(), ef);
        }
        files = new ArrayList<>(uniqueFiles.values());
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        // create the ZIP file
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        // compress the files
        for (int i = 0; i < files.size(); i++) {
            File file = new File(files.get(i).getFullFilePathAndName());
            FileInputStream in = new FileInputStream(file);
            // add ZIP entry to output stream
            String relativeFolder = files.get(i).getRelativeFolder();
            if(relativeFolder!=null) {


                out.putNextEntry(new ZipEntry(relativeFolder + "/" + file.getName()));
            }else {
                out.putNextEntry(new ZipEntry( file.getName()));
            }
            // transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // complete the entry
            out.closeEntry();
            in.close();
        }
        // complete the ZIP file
        out.close();
        for(ExportFile ef:files){
            ef.deleteFile();
        }
    }

}
