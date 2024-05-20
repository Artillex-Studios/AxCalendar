package com.artillexstudios.axcalendar.utils;

import com.artillexstudios.axapi.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class ActionUtils {

    public static void handleAction(@NotNull HumanEntity player, @NotNull String str, int day) {
        if (str.startsWith("[CLOSE]")) {
            player.closeInventory();
            return;
        }

        if (str.startsWith("[CONSOLE]")) {
            str = str.replace("[CONSOLE] ", "");
            final String msg = str.replace("%day%", "" + day).replace("%player%", player.getName());
            Scheduler.get().executeAt(player.getLocation(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg));
            return;
        }

        if (str.startsWith("[MESSAGE]")) {
            str = str.replace("[MESSAGE] ", "");
            MESSAGEUTILS.sendFormatted(player, str.replace("%day%", "" + day).replace("%player%", player.getName()));
            return;
        }

        if (str.startsWith("[PLAYER]")) {
            str = str.replace("[PLAYER] ", "");
            final String msg = str.replace("%day%", "" + day).replace("%player%", player.getName());
            Scheduler.get().executeAt(player.getLocation(), () -> Bukkit.dispatchCommand(player, msg));
            return;
        }

        if (str.startsWith("[SOUND]")) {
            str = str.replace("[SOUND] ", "");
            ((Player) player).playSound(player.getLocation(), Sound.valueOf(str), 1, 1);
        }
    }
}
