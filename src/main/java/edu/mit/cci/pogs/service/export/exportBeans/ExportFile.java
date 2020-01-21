package edu.mit.cci.pogs.service.export.exportBeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ExportFile {

    private String fileName;
    private String fileHeader;
    private String fileContent;
    private String relativeFolder;
    private String fileRootPath;
    private String fullFilePathAndName;
    private String fileType;

    public void deleteFile(){
        File file = new File(this.getFullFilePathAndName());
        if(file!=null && file.exists()){
            file.delete();
        }
    }
    public void writeContents() {
        PrintWriter writer1 = null;
        try {

            fullFilePathAndName = fileRootPath +
                    ((relativeFolder!= null)?(relativeFolder + File.separatorChar):("")) + fileName;

            if(relativeFolder!=null){
                File f = new File(fileRootPath +
                        ((relativeFolder!= null)?(relativeFolder + File.separatorChar):("")));
                f.mkdirs();
            }

            writer1 = new PrintWriter(new OutputStreamWriter
                    (new FileOutputStream(fullFilePathAndName), "UTF-8"));
            if(fileHeader!=null) {
                writer1.println(fileHeader);
            }
            writer1.print(fileContent);
            writer1.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getRelativeFolder() {
        return relativeFolder;
    }

    public void setRelativeFolder(String relativeFolder) {
        this.relativeFolder = relativeFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(String fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileRootPath() {
        return fileRootPath;
    }

    public void setFileRootPath(String fileRootPath) {
        this.fileRootPath = fileRootPath;
    }

    public String getFullFilePathAndName() {
        return fullFilePathAndName;
    }

    public void setFullFilePathAndName(String fullFilePathAndName) {
        this.fullFilePathAndName = fullFilePathAndName;
    }
}