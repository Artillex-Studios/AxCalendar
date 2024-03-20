package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axcalendar.commands.Commands;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class SubCommandReload {

    public void subCommand(@NotNull CommandSender sender) {

        final String errorMsg = CONFIG.getString("prefix") + MESSAGES.getString("reload.failed");

        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }

        if (!MESSAGES.reload()) {
            MESSAGEUTILS.sendFormatted(sender, errorMsg, Map.of("%file%", "messages.yml"));
            return;
        }

        CalendarUtils.reload();
        MESSAGEUTILS.sendLang(sender, "reload.success");
        Commands.registerCommand();
    }
}
