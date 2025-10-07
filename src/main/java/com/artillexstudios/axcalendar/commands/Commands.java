package com.artillexstudios.axcalendar.commands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.commands.subcommands.Open;
import com.artillexstudios.axcalendar.commands.subcommands.Reload;
import com.artillexstudios.axcalendar.commands.subcommands.Reset;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import com.artillexstudios.axcalendar.utils.CommandMessages;
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

import java.util.Locale;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.LANG;

public class Commands implements OrphanCommand {

    @DefaultFor({"~", "~ open"})
    public void open(@NotNull Player sender) {
        Open.INSTANCE.execute(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        for (String m : LANG.getStringList("help")) {
            sender.sendMessage(StringUtils.formatToString(m));
        }
    }

    @Subcommand("reload")
    @CommandPermission("axcalendar.admin")
    public void reload(@NotNull CommandSender sender) {
        Reload.INSTANCE.execute(sender);
    }

    @Subcommand("reset")
    @CommandPermission("axcalendar.admin")
    public void reset(@NotNull CommandSender sender, OfflinePlayer player) {
        Reset.INSTANCE.execute(sender, player);
    }

    @Subcommand("debuginfo")
    @CommandPermission("axcalendar.admin")
    public void debuginfo(@NotNull CommandSender sender) {
        sender.sendMessage(StringUtils.formatToString("&#FF0000Current milliseconds: &f" + CalendarUtils.getZonedDateTime().toInstant().toEpochMilli()));
        sender.sendMessage(StringUtils.formatToString("&#FF0000Date: &f" + CalendarUtils.getZonedDateTime()));
    }

    private static BukkitCommandHandler handler = null;

    public static void registerCommand() {
        if (handler == null) {
            handler = BukkitCommandHandler.create(AxCalendar.getInstance());

            handler.getTranslator().add(new CommandMessages());
            handler.setLocale(new Locale("en", "US"));
        }

        handler.unregisterAllCommands();
        handler.register(Orphans.path(CONFIG.getStringList("command-aliases").toArray(String[]::new)).handler(new Commands()));
        handler.registerBrigadier();
    }
}
