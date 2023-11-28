package com.artillexstudios.axcalendar.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;

public class TimeUtils {

    @NotNull
    public static String formatTime(long time) {

        if (time < 0) return "---";

        Duration remainingTime = Duration.ofMillis(time);
        long total = remainingTime.getSeconds();
        long days = total / 86400;
        long hours = (total % 86400) / 3600;
        long minutes = (total % 3600) / 60;
        long seconds = total % 60;

        if (CONFIG.getInt("timer-format", 1) == 1) {
            if (days > 0) return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
            if (hours > 0) return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            if (days > 0) return days + MESSAGES.getString("time.day", "d");
            if (hours > 0) return hours + MESSAGES.getString("time.hour", "h");
            if (minutes > 0) return minutes + MESSAGES.getString("time.minute", "m");
            return seconds + MESSAGES.getString("time.second", "s");
        }
    }
}
