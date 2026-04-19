package com.lfj.messenger.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class Time {
    private final static ZoneId TIME_ZONE = ZoneId.systemDefault();
    private static DateTimeFormatter UI_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss").withZone(ZoneId.systemDefault());
    private static DateTimeFormatter UI_DAY_AND_MONTH_FORMAT = DateTimeFormatter.ofPattern("dd MMMM").withZone(ZoneId.systemDefault());
    private Time(){
    }
    public static String getTime(Instant instant){
        return instant.atZone(TIME_ZONE).format(UI_FORMAT);
    }
    public static String getDayAndMonthTime(Instant instant){
        return instant.atZone(TIME_ZONE).format(UI_DAY_AND_MONTH_FORMAT);
    }
    public static Instant nowInstant(){return Instant.now();}
}
