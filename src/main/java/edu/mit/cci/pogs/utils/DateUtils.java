package edu.mit.cci.pogs.utils;

import java.util.Date;

public class DateUtils {

    public static long now() {
        return new Date().getTime();
    }

    public static long toMilliseconds(Integer v) {
        return v * 1000;
    }
}
