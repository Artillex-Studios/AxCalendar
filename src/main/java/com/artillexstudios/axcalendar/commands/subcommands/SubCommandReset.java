package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class SubCommandReset {

    public void subCommand(@NotNull CommandSender sender, OfflinePlayer player) {

        if (player.getName() == null) {
            MESSAGEUTILS.sendLang(sender, "reset.failed", Map.of("%player%", player.getName()));
            return;
        }

        AxCalendar.getThreadedQueue().submit(() -> AxCalendar.getDatabase().reset(player.getUniqueId()));
        MESSAGEUTILS.sendLang(sender, "reset.success", Map.of("%player%", player.getName()));
    }
}
