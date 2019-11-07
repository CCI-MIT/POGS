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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.model.dao.session.SessionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.service.export.SessionExportService;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.utils.ExportUtils;


@RestController
public class ExportDataController {

    @Autowired
    private SessionExportService sessionExportService;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private Environment env;

    @GetMapping("/admin/export/session/{sessionId}")
    public void exportSessionData(HttpServletRequest request, HttpServletResponse response, @PathVariable("sessionId") Long sessionId) {
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Session_"+sessionId+"_Report_" + ExportUtils.getTimeFormattedNoSpaces(new Timestamp(new Date().getTime())) + ".zip");



            List<ExportFile> sessionExportFiles = sessionExportService.getSessionExportFiles(sessionId,null, getPath(request));

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
    @GetMapping("/admin/export/study/{studyId}")
    public void getStudyData(HttpServletRequest request,
                             HttpServletResponse response, @PathVariable("studyId") Long studyId) {

        List<ExportFile> exportFiles = new ArrayList<>();
        List<Session> sessionInStudy = sessionDao.listByStudyId(studyId);

        for (Session session : sessionInStudy) {
            exportFiles.addAll(sessionExportService.getSessionExportFiles(session.getId(), studyId, getPath(request)));
        }

        //group the content of similar files.
        Map<String,ExportFile> filesGroupedByType = new LinkedHashMap<>();

        for(ExportFile exportFile: exportFiles){
            if(filesGroupedByType.get(exportFile.getFileType())==null){
                filesGroupedByType.put(exportFile.getFileType(),exportFile);
            } else {
                StringBuffer sb = new StringBuffer(filesGroupedByType.get(exportFile.getFileType()).getFileContent());
                sb.append(exportFile.getFileContent());
                filesGroupedByType.get(exportFile.getFileType()).setFileContent(sb.toString());
            }
        }
        exportFiles = new ArrayList<>();
        for(String fileType: filesGroupedByType.keySet()){
            exportFiles.add(filesGroupedByType.get(fileType));
        }
        exportFiles.add(sessionExportService.exportAggregateSubjectParticipation(studyId, getPath(request)));

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Study_"+studyId+"_Report_" + ExportUtils.getTimeFormattedNoSpaces(new Timestamp(new Date().getTime())) + ".zip");



            filesToZip(response, exportFiles);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void filesToZip(HttpServletResponse response, List<ExportFile> files) throws IOException {
        for(ExportFile ef:files){
            ef.writeContents();
        }
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
    @GetMapping("/admin/export/subjectcontribution/{studyId}")
    public void getSubjectContribution(
            HttpServletRequest request,
            HttpServletResponse response, @PathVariable("studyId") Long studyId) {
        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition",
                    "attachment; filename=Subject_Contribution_"+studyId+"_Report_" + ExportUtils.getTimeFormattedNoSpaces(new Timestamp(new Date().getTime())) + ".zip");



            List<ExportFile> sessionExportFiles = new ArrayList<>();

            sessionExportFiles.add(sessionExportService.exportAggregateSubjectParticipation(studyId, getPath(request)));

            filesToZip(response, sessionExportFiles);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/admin/export/taskscore/{studyId}")
    public void getTaskScoreData(HttpServletResponse response,
                                 HttpServletRequest request,
                                 @PathVariable("studyId") Long studyId) {

        try {
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=TaskScoresReport_" + new Date().toString() + ".zip");

            List<ExportFile> sessionExportFiles = new ArrayList<>();

            sessionExportFiles.add(sessionExportService.exportAggregateStudyScoreReport(studyId, getPath(request)));

            filesToZip(response, sessionExportFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
