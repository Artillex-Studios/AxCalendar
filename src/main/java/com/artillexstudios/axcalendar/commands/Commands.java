package com.artillexstudios.axcalendar.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandReload;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;

@Command({"axcalendar", "calendar", "adventcalendar", "axadventcalendar"})
public class Commands {

    @DefaultFor({"~"})
    public void open(@NotNull Player sender) {
        final Gui menu = Gui.gui()
                .title(StringUtils.format(MESSAGES.getString("menu.title")))
                .rows(MESSAGES.getInt("menu.title", 6))
                .disableAllInteractions()
                .create();

        menu.open(sender);
    }

    @Subcommand("help")
    public void help(@NotNull Player sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axcalendar.admin")
    public void reload(@NotNull Player sender) {
        new SubCommandReload().subCommand(sender);
    }
}
