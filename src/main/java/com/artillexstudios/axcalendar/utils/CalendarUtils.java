package com.artillexstudios.axcalendar.utils;

import java.time.Month;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class CalendarUtils {
    private static Calendar calendar;

    static {
        reload();
    }

    public static void reload() {
        if (!CONFIG.getString("timezone", "").isEmpty()) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(CONFIG.getString("timezone", "")));
        } else {
            calendar = Calendar.getInstance();
        }
    }

    public static int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameMonth() {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US).equalsIgnoreCase(CONFIG.getString("month", "DECEMBER"));
    }

    public static long getMilisUntilDay(int day) {
        final Calendar calendar1 = (Calendar) calendar.clone();
        calendar1.set(Calendar.MONTH, Month.valueOf(CONFIG.getString("month", "DECEMBER")).ordinal());
        calendar1.set(Calendar.DAY_OF_MONTH, day);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        return calendar1.getTimeInMillis() - System.currentTimeMillis();
    }
}
