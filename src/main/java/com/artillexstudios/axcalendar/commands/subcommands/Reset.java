package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axcalendar.AxCalendar;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public enum Reset {
    INSTANCE;

    public void execute(@NotNull CommandSender sender, OfflinePlayer player) {
        if (player.getName() == null) {
            MESSAGEUTILS.sendLang(sender, "reset.failed", Map.of("%player%", player.getName()));
            return;
        }

        AxCalendar.getThreadedQueue().submit(() -> AxCalendar.getDatabase().reset(player));
        MESSAGEUTILS.sendLang(sender, "reset.success", Map.of("%player%", player.getName()));
    }
}
