package com.artillexstudios.axcalendar.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class CalendarUtils {
    private static ZoneId zoneId;
    private static int offset;

    public static void reload() {
        if (!CONFIG.getString("timezone", "").isEmpty()) {
            zoneId = ZoneId.of(CONFIG.getString("timezone", ""));
        } else {
            zoneId = ZoneId.systemDefault();
        }
        offset = CONFIG.getInt("timezone-offset", 0);
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
        final String month = CONFIG.getString("month", "DECEMBER");
        return month.equalsIgnoreCase("AUTO") || getZonedDateTime().getMonth().getDisplayName(TextStyle.FULL, Locale.US).equalsIgnoreCase(month);
    }

    public static long getMilisUntilDay(int day) {
        final String monthStr = CONFIG.getString("month", "DECEMBER");
        Month month = monthStr.equalsIgnoreCase("AUTO") ? getZonedDateTime().getMonth() : Month.valueOf(monthStr);
        final ZonedDateTime startOfTomorrow = getZonedDateTime().withMonth(month.getValue()).withDayOfMonth(day).toLocalDate().atStartOfDay(zoneId);
        final Duration duration = Duration.between(getZonedDateTime(), startOfTomorrow);
        return duration.toMillis();
    }
}
