package com.lfj.messager.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public final class Time {
    private static ZoneId TIME_ZONE = ZoneId.systemDefault();
    private static DateTimeFormatter UI_FORMAT = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .ofPattern("yyyy-MM-dd | HH:mm:ss");
    private Time(){
    }
    public static String getTime(Instant instant){
        return instant.atZone(TIME_ZONE).format(UI_FORMAT);
    }
    public static Instant nowInstant(){return Instant.now();}
}
