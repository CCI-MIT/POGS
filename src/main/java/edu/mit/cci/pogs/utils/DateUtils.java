package edu.mit.cci.pogs.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    public static long today(){
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND,0);
        return cal.getTime().getTime();
    }

}
