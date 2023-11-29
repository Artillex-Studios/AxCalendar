package com.artillexstudios.axcalendar.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandOpen;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandReload;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandReset;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;

@Command({"axcalendar", "calendar", "adventcalendar", "axadventcalendar"})
public class Commands {

    @DefaultFor({"~", "~ open"})
    public void open(@NotNull Player sender) {
        new SubCommandOpen().subCommand(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        for (String m : MESSAGES.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axcalendar.admin")
    public void reload(@NotNull CommandSender sender) {
        new SubCommandReload().subCommand(sender);
    }

    @Subcommand("reset")
    @CommandPermission("axcalendar.admin")
    public void reset(@NotNull CommandSender sender, OfflinePlayer player) {
        new SubCommandReset().subCommand(sender, player);
    }
}
