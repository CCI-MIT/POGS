package edu.mit.cci.pogs.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static long now() {
        return new Date().getTime();
    }

    public static long toMilliseconds(Integer v) {
        return v * 1000;
    }

    public static String getTimeFormatted(Timestamp timestamp) {
        if (timestamp == null) return "";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(new Date(timestamp.getTime()));
    }

}
