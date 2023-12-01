package com.artillexstudios.axcalendar.utils;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class RequirementUtils {

    public static boolean canClaim(@NotNull Player player) {
        if (CONFIG.getStringList("claim-requirements") == null || CONFIG.getStringList("claim-requirements").isEmpty()) return true;

        for (String str : CONFIG.getStringList("claim-requirements")) {
            final String[] ar = str.split(" ");

            switch (ar[0]) {
                case "[PLAYTIME]" -> {
                    return (player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 60 / 20) >= Integer.parseInt(ar[1]);
                }

                case "[PERMISSION]" -> {
                    return player.hasPermission(ar[1]);
                }
            }
        }

        return false;
    }
}
