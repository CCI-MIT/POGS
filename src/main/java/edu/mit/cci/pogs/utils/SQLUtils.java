package edu.mit.cci.pogs.utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

public class SQLUtils {

    public static String getBasicSetup(){
        String saida = new String();
        saida = "";
        saida+="INSERT INTO `auth_user` VALUES (1,'admin@pogs.info','$2a$10$TCuFoMKkAo4o5b7yWpRN8.RH8H4brfPdEhkhHEg8YzqmerB5u5A0i','Pogs','Admin',true);\n";
        saida+="INSERT INTO `research_group` (`id`,`group_name`) VALUES (1,'superAdminGroup');\n";
        saida+="INSERT INTO `research_group_has_auth_user` (`id`,`research_group_id`,`auth_user_id`) VALUES (1,1,1);\n";
        return saida;
    }

    public static String getSQLInsertFromPojo(Object pojo) {

        Method[] methods = pojo.getClass().getMethods();
        String sqlFields = "";
        String sqlValues = "";

        int counter =0;
        for (int i = 0; i < methods.length; i++) {

            if (methods[i].getName().startsWith("get")&& methods[i].getName()!="getClass") {

                try {
                    String name = methods[i].getName().replaceAll("^get", "");
                    name = camelToSnake(name);

                    Object objectValue = methods[i].invoke(pojo);
                    String value = (objectValue==null)?("NULL"):(objectValue.toString());
                    if (counter != 0) {
                        sqlFields = sqlFields + ",";
                        sqlValues = sqlValues + ",";
                    }
                    if (objectValue instanceof String) {
                        sqlValues = sqlValues + "'" + escapeStringForInsert(value) + "'";
                    } else {
                        if (objectValue instanceof Timestamp) {
                            sqlValues = sqlValues + "'" + DateUtils
                                    .getTimeFormatted((Timestamp) objectValue) + "'";
                        } else {
                            sqlValues = sqlValues + value;
                        }
                    }
                    sqlFields = sqlFields + "`"+name+"`";
                } catch (IllegalAccessException | InvocationTargetException e) {

                }
                counter++;
            }

        }
        String sql = "INSERT INTO `" + camelToSnake(pojo.getClass().getSimpleName()) +
                "` (" + sqlFields + ") values (" + sqlValues + ");\n";
        //System.out.println(sql);
        return sql;
    }

    private static String escapeStringForInsert(String value) {
        return StringEscapeUtils.escapeEcmaScript(value);
        //StringEscapeUtils.escape
    }

    public static String camelToSnake(String str) {
        // Regular Expression
        String regex = "([a-z])([A-Z]+)";

        // Replacement string
        String replacement = "$1_$2";

        // Replace the given regex
        // with replacement string
        // and convert it to lower case.
        str = str
                .replaceAll(
                        regex, replacement)
                .toLowerCase();

        // return string
        return str;
    }
}
