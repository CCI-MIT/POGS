package edu.mit.cci.pogs.view.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.mit.cci.pogs.model.dao.fileentry.FileEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.FileEntry;

@RestController
public class FileUploadController {

    @Autowired
    private FileEntryDao fileEntryDao;

    private ImageResponse uploadImage(MultipartFile file, HttpServletRequest request) {

        try {
            String path = request.getSession().getServletContext().getRealPath("/");

            byte[] bytes = file.getBytes();

            FileEntry fileEntry = new FileEntry();
            fileEntry.setCreateDate(new Timestamp(new Date().getTime()));
            String nameExt = file.getOriginalFilename();
            fileEntry.setFileEntryExtension(FilenameUtils.getExtension(nameExt).toLowerCase());
            fileEntry.setFileSize(bytes.length);
            fileEntry.setFileEntryName(FilenameUtils.getName(nameExt));


            fileEntry = fileEntryDao.create(fileEntry);

            String finalPath = path + "../fileEntries/" + File.separator;
            File folder = new File(finalPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                FileUtils.writeByteArrayToFile(new File(finalPath + fileEntry.getId() + "." + fileEntry.getFileEntryExtension()), bytes);
            } catch (IOException e) {

            }

            final String imageIdString = String.valueOf(fileEntry.getId());
            return new ImageResponse(imageIdString, "/images/" + fileEntry.getId(), true, "");


        } catch (IOException e) {
            return new ImageResponse(null, null, false, e.getMessage());
        }

    }

    @PostMapping("/images/upload")
    public void fileImageUpload(@RequestParam("file") MultipartFile file,
                                HttpServletRequest request, HttpServletResponse response) {

        ImageResponse ir = uploadImage(file, request);
        try {
            response.setContentType("text/html");
            response.getOutputStream()
                    .write((
                            ir.getImageUrl()).getBytes());
        } catch (IOException ignored) {

        }
    }

    @GetMapping("/images/{fileEntryId}")
    public void serveImage(HttpServletRequest request, HttpServletResponse response,
                           @PathVariable long fileEntryId)
            throws IOException {

        String path = request.getSession().getServletContext().getRealPath("/");
        String finalPath = path + "../fileEntries/" + File.separator;

        FileEntry fileEntry = fileEntryDao.get(fileEntryId);

        if (fileEntry == null) {
            throw new IOException("Cannot find file for id" + fileEntryId);
        }

        File file = new File(finalPath + fileEntry.getId() + "." + fileEntry.getFileEntryExtension());

        final ServletContext servletContext = request.getServletContext();
        final String mimeType = servletContext.getMimeType(file.getAbsolutePath());
        if (mimeType == null) {
            throw new IOException("Cannot resolve mime type for file " + file.getAbsolutePath());
        }

        response.setContentType(mimeType);

        try (FileInputStream in = new FileInputStream(file)) {
            final int count = IOUtils.copy(in, response.getOutputStream());
            response.setContentLength(count);
        }

    }

    private static class ImageResponse {

        private final String imageId;
        private final String imageUrl;
        private final boolean success;
        private final String message;

        private ImageResponse(String imageId, String imageUrl, boolean success, String message) {
            this.imageId = imageId;
            this.imageUrl = imageUrl;
            this.success = success;
            this.message = message;
        }

        public String getImageId() {
            return imageId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
