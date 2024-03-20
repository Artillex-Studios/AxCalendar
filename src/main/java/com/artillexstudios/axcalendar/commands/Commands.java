package com.artillexstudios.axcalendar.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandOpen;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandReload;
import com.artillexstudios.axcalendar.commands.subcommands.SubCommandReset;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;

public class Commands implements OrphanCommand {

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

    @Subcommand("debuginfo")
    @CommandPermission("axcalendar.admin")
    public void debuginfo(@NotNull CommandSender sender) {
        sender.sendMessage(StringUtils.formatToString("&#FF0000Current miliseconds: &f" + CalendarUtils.getZonedDateTime().toInstant().toEpochMilli()));
        sender.sendMessage(StringUtils.formatToString("&#FF0000Date: &f" + CalendarUtils.getZonedDateTime()));
    }

    public static void registerCommand() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(AxCalendar.getInstance());
        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("command-aliases").toArray(String[]::new)).handler(new Commands()));
        handler.registerBrigadier();
    }
}
