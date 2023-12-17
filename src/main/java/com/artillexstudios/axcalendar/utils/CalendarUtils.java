package com.artillexstudios.axcalendar.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class CalendarUtils {
    private static ZoneId zoneId;
    private static int offset;

    static {
        reload();
    }

    public static void reload() {
        if (!CONFIG.getString("timezone", "").isEmpty()) {
            zoneId = ZoneId.of(CONFIG.getString("timezone", ""));
            offset = CONFIG.getInt("timezone-offset", 0);
        } else {
            zoneId = ZoneId.systemDefault();
            offset = CONFIG.getInt("timezone-offset", 0);
        }
    }

    public static int getDayOfMonth() {
        return getZonedDateTime().getDayOfMonth();
    }

    @NotNull
    public static ZonedDateTime getZonedDateTime() {
        ZonedDateTime zdt = ZonedDateTime.now(Clock.system(zoneId));
        zdt = zdt.plusHours(offset);
        return zdt;
    }

    public static boolean isSameMonth() {
        return getZonedDateTime().getMonth().getDisplayName(TextStyle.FULL, Locale.US).equalsIgnoreCase(CONFIG.getString("month", "DECEMBER"));
    }

    public static long getMilisUntilDay(int day) {
        final ZonedDateTime startOfTomorrow = getZonedDateTime().withDayOfMonth(day).toLocalDate().atStartOfDay(zoneId);
        final Duration duration = Duration.between(getZonedDateTime(), startOfTomorrow);
        return duration.toMillis();
    }
}
