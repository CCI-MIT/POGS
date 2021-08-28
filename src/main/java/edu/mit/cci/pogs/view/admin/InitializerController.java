package edu.mit.cci.pogs.view.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.mit.cci.pogs.service.UserService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.model.jooq.tables.pojos.FileEntry;
import edu.mit.cci.pogs.view.file.FileUploadController;

@Controller
public class InitializerController {

    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;

    private static final int BUFFER_SIZE = 4096;

    @Autowired
    private DSLContext dslContext;

    @PostMapping("/initialize")
    public String fileImageUpload(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response, Model model) {
        String ir = uploadImage(file, request);
        return forwardToHTML(model);
    }

    private String uploadImage(MultipartFile file, HttpServletRequest request) {

        try {

            String path = env.getProperty("images.dir");
            if (path == null) {
                path = request.getSession().getServletContext().getRealPath("/");
            }

            byte[] bytes = file.getBytes();

            FileEntry fileEntry = new FileEntry();
            fileEntry.setCreateDate(new Timestamp(new Date().getTime()));
            String nameExt = file.getOriginalFilename();
            fileEntry.setFileEntryExtension(FilenameUtils.getExtension(nameExt).toLowerCase());
            fileEntry.setFileSize(bytes.length);
            fileEntry.setFileEntryName(FilenameUtils.getName(nameExt));


            String finalPath = path + "/fileEntries" + File.separator;
            File folder = new File(finalPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            FileUtils.writeByteArrayToFile(new File(finalPath + fileEntry.getId() + "." + fileEntry.getFileEntryExtension()), bytes);
            unzip(finalPath + fileEntry.getId() + "." + fileEntry.getFileEntryExtension(), path + "/fileEntries/");
            String finalPathForDump = path + "/fileEntries/dump.sql";
            String fileContests = getFileContents(finalPathForDump);
            runSQLFromFile(fileContests);


        } catch (IOException e) {
        }
        return "";
    }
    public static String getFileContents(String filepath) throws IOException {
        InputStream is = new FileInputStream(filepath);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while(line != null){ sb.append(line).append("\n"); line = buf.readLine(); }
        String fileAsString = sb.toString();
        return fileAsString;
    }
    public void runSQLFromFile(String contents ){

        String [] lines = contents.split("\n");
        for(String line: lines) {
            if(!line.isEmpty()) {
                try {
                    dslContext.execute(line);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                new File(filePath).getParentFile().mkdirs();
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }


    @GetMapping("/initialize")
    public String showIndex(Model model) {
        return forwardToHTML(model);
    }

    private String forwardToHTML(Model model) {
        if(userService.hasAuthUsers()){
            model.addAttribute("alreadyInitialized", true);
        } else {
            model.addAttribute("alreadyInitialized", false);
        }

        return "initialize";
    }
}
