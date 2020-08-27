package edu.mit.cci.pogs.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTask;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.CompletedTaskScore;
import edu.mit.cci.pogs.model.jooq.tables.pojos.EventLog;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskExecutionAttribute;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;

public class ExportUtils {


    private static String[] ATTRIBUTES_TO_EXTERNALIZE_IF_JSON_OR_TOO_BIG = {"getEventContent", "getStringValue", "getScoringData"};


    public static <T> List<ExportFile> getEntityDataExportFile(String path, Class<T> clazz, List<T> list,
                                                               Long studyId, Long sessionId,
                                                               Map<Long, Long> completedTaskIdsToSessionsMap, Long entitySpecificId) {

        String tmpdir = path;
        List<ExportFile> ret = new ArrayList<>();
        ExportFile ef = new ExportFile();
        Timestamp now = new Timestamp(new Date().getTime());
        boolean isStudyExport = false;
        ef.setFileRootPath(tmpdir);
        if (studyId != null) {
            isStudyExport = true;

            ef.setFileName(clazz.getSimpleName() + ((entitySpecificId != null) ? ("_" + entitySpecificId) : ("")) + "_study_" + String.valueOf(studyId) + "_" + getTimeFormattedNoSpaces(now) + ".csv");
        } else {
            ef.setFileName(clazz.getSimpleName() + ((entitySpecificId != null) ? ("_" + entitySpecificId) : ("")) + "_session_" + String.valueOf(sessionId) + "_" + getTimeFormattedNoSpaces(now) + ".csv");
        }

        Method[] methods = clazz.getDeclaredMethods();
        StringBuffer header = getHeaderForEntity(methods, isStudyExport);

        StringBuffer content = new StringBuffer();

        Long entryId = null;
        for (T element : list) {
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    try {
                        if (method.getName().equals("getId")) {
                            entryId = (Long) method.invoke(element, null);
                        }
                        if (method.getName().equals(ATTRIBUTES_TO_EXTERNALIZE_IF_JSON_OR_TOO_BIG[0]) ||
                                method.getName().equals(ATTRIBUTES_TO_EXTERNALIZE_IF_JSON_OR_TOO_BIG[1]) ||
                                method.getName().equals(ATTRIBUTES_TO_EXTERNALIZE_IF_JSON_OR_TOO_BIG[2])) {
                            String contentString = (String) method.invoke(element, null);
                            boolean isJson = StringUtils.isJSONValid(contentString);
                            if (isJson || (contentString != null && contentString.length() > 500)) {
                                ExportFile exportFile = new ExportFile();
                                exportFile.setFileContent(contentString);
                                exportFile.setFileRootPath(tmpdir);
                                StringUtils.isJSONValid(contentString);

                                if (studyId != null) {
                                    exportFile.setRelativeFolder(clazz.getSimpleName());
                                    exportFile.setFileName(
                                            clazz.getSimpleName()
                                                    + "_" + entryId + "_study_" + String.valueOf(studyId)
                                                    + "_" + getTimeFormattedNoSpaces(now) +
                                                    ((isJson) ? (".json") : (".txt")));
                                } else {
                                    exportFile.setRelativeFolder(clazz.getSimpleName());
                                    exportFile.setFileName(
                                            clazz.getSimpleName() + "_" + entryId +
                                                    "_session_" + String.valueOf(sessionId)
                                                    + "_" + getTimeFormattedNoSpaces(now) +
                                                    ((isJson) ? (".json") : (".txt")));
                                }
                                exportFile.setFileType(clazz.getSimpleName() + "_" + entryId);
                                ret.add(exportFile);
                                content.append("In file: " + clazz.getSimpleName() + "_" + entryId);

                            } else {
                                content.append(contentString);
                            }
                        } else {
                            if (method.getReturnType() == Timestamp.class) {
                                Timestamp timestamp = (Timestamp) method.invoke(element, null);
                                content.append(getTimeFormatted(timestamp));
                            } else {
                                content.append(method.invoke(element, null));
                            }
                        }
                        content.append(",");
                    } catch (InvocationTargetException ite) {
                        ite.printStackTrace();
                    } catch (IllegalAccessException iae) {
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

        ef.setFileHeader(header.toString());
        ef.setFileType(clazz.getSimpleName());
        ef.setFileContent(content.toString());
        ret.add(ef);
        return ret;

    }
    public static String cleanEntry(String summaryDescription) {
        if (summaryDescription == null) return "";
        summaryDescription = summaryDescription.replace("/\n/g", "¶");
        summaryDescription = summaryDescription.replace("/\r/g", "");
        summaryDescription = summaryDescription.replace("/,/g", "|");
        summaryDescription = summaryDescription.replace("/;/g", "|");
        return summaryDescription;
    }

    public static <T> ExportFile getExportFileForSimplePojoWithExtraColumns(String path, String fileName ,
                                                            Class<T> clazz,
                                                            List<T> list ,
                                                            List<String> methodOrder,
                                                            List<String> extraColumns) {

        String tmpdir = path;

        ExportFile ef = new ExportFile();
        Timestamp now = new Timestamp(new Date().getTime());

        ef.setFileRootPath(tmpdir);


        ef.setFileName(fileName+".csv");


        Method[] methods = clazz.getDeclaredMethods();
        StringBuffer header = getHeaderForMethods(methodOrder, false);

        StringBuffer content = new StringBuffer();

        for (T element : list) {
            for(String metho: methodOrder) {
                for (Method method : methods) {

                    if(method.getName().equals("get"+ capitalizeFirst(metho))) {

                        try {
                            if (method.getReturnType() == Timestamp.class) {
                                Timestamp timestamp = (Timestamp) method.invoke(element, null);
                                content.append(getTimeFormatted(timestamp));
                            } else {
                                Object rv = method.invoke(element, null);
                                if(rv!=null) {
                                    content.append(rv);
                                } else {
                                    content.append("");
                                }
                            }

                            content.append(";");
                        } catch (InvocationTargetException ite) {
                            ite.printStackTrace();
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    }
                }
            }
            for (Method method : methods) {
                if(method.getName().equals("getExtraColumns")){
                    try {
                        Object rv = method.invoke(element, null);
                        if (rv != null) {
                            content.append(rv);
                        }
                    }catch (InvocationTargetException ite){
                        ite.printStackTrace();;
                    }catch (IllegalAccessException ite){
                        ite.printStackTrace();
                    }
                }
            }

            content.deleteCharAt(content.length() - 1);
            content.append("\n");
        }
        header.append(";");
        for(String column: extraColumns ) {
            header.append(column + ";");
        }
        ef.setFileHeader(header.toString());
        ef.setFileType(clazz.getSimpleName());
        ef.setFileContent(content.toString());

        return ef;
    }
    public static <T> ExportFile getExportFileForSimplePojo(String path, String fileName ,
                                                                  Class<T> clazz,
                                                               List<T> list , List<String> methodOrder) {

        String tmpdir = path;

        ExportFile ef = new ExportFile();
        Timestamp now = new Timestamp(new Date().getTime());

        ef.setFileRootPath(tmpdir);


        ef.setFileName(fileName+".csv");


        Method[] methods = clazz.getDeclaredMethods();
        StringBuffer header = getHeaderForMethods(methodOrder, false);

        StringBuffer content = new StringBuffer();

        for (T element : list) {
            for(String metho: methodOrder) {
                for (Method method : methods) {

                    if(method.getName().equals("get"+ capitalizeFirst(metho))) {

                        try {
                            if (method.getReturnType() == Timestamp.class) {
                                Timestamp timestamp = (Timestamp) method.invoke(element, null);
                                content.append(getTimeFormatted(timestamp));
                            } else {
                                Object rv = method.invoke(element, null);
                                if(rv!=null) {
                                    content.append(rv);
                                } else {
                                    content.append("");
                                }
                            }

                            content.append(";");
                        } catch (InvocationTargetException ite) {
                            ite.printStackTrace();
                        } catch (IllegalAccessException iae) {
                            iae.printStackTrace();
                        }
                    }
                }
            }
            content.deleteCharAt(content.length() - 1);
            content.append("\n");
        }

        ef.setFileHeader(header.toString());
        ef.setFileType(clazz.getSimpleName());
        ef.setFileContent(content.toString());

        return ef;

    }

    private static String capitalizeFirst(String metho) {
        return (metho.substring(0,1)).toUpperCase() + metho.substring(1,metho.length());
    }

    public static String getTimeFormattedNoSpaces(Timestamp timestamp) {
        String pattern = "yyyy_MM_dd_HH_mm_ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(timestamp.getTime()));
    }

    public static String getTimeFormatted(Timestamp timestamp) {
        if (timestamp == null) return "";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(timestamp.getTime()));
    }
    public static StringBuffer getHeaderForMethods(List<String> methods, boolean isStudyExport) {


        StringBuffer header = new StringBuffer();
        for (int i = 0;i < methods.size(); i++) {
            String s = methods.get(i);
            header.append(capitalizeFirst(s));
            if(i+1!=methods.size()){
                header.append(";");
            }
        }

        return header;
    }

    public static StringBuffer getHeaderForEntity(Method[] methods, boolean isStudyExport) {

        Map<String, String> headers = new LinkedHashMap<>();
        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                String aux = method.getName().replaceFirst("get", "");
                headers.put(aux, aux);
            }
        }
        if (isStudyExport) {
            headers.put("SessionId", "SessionId");
        }

        StringBuffer header = new StringBuffer();
        for (String s : headers.values()) {
            header.append(s + ",");
        }

        return header;
    }
}
