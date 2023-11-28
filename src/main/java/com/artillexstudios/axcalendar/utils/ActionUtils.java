package com.artillexstudios.axcalendar.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class ActionUtils {

    public static void handleAction(@NotNull HumanEntity player, @NotNull String str, int day) {
        if (str.startsWith("[CLOSE]")) {
            player.closeInventory();
        }

        if (str.startsWith("[CONSOLE]")) {
            str = str.replace("[CONSOLE] ", "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("%day%", "" + day).replace("%player%", player.getName()));
        }

        if (str.startsWith("[MESSAGE]")) {
            str = str.replace("[MESSAGE] ", "");
            MESSAGEUTILS.sendFormatted(player, str.replace("%day%", "" + day).replace("%player%", player.getName()));
        }
    }
}
