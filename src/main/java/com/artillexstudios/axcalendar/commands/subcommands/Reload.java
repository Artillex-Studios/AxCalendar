package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.gui.data.MenuManager;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.LANG;
import static com.artillexstudios.axcalendar.AxCalendar.MENU;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;
import static com.artillexstudios.axcalendar.AxCalendar.REWARDS;

public enum Reload {
    INSTANCE;

    public void execute(@NotNull CommandSender sender) {
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055[AxCalendar] &#FFAAAAReloading configuration..."));
        if (!CONFIG.reload()) {
            MESSAGEUTILS.sendLang(sender, "reload.failed", Map.of("%file%", "config.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055╠ &#FFAAAAReloaded &fconfig.yml&#FFAAAA!"));

        if (!LANG.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "lang.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055╠ &#FFAAAAReloaded &flang.yml&#FFAAAA!"));

        if (!MENU.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "menu.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055╠ &#FFAAAAReloaded &fmenu.yml&#FFAAAA!"));

        if (!REWARDS.reload()) {
            MESSAGEUTILS.sendFormatted(sender, "reload.failed", Map.of("%file%", "rewards.yml"));
            return;
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055╠ &#FFAAAAReloaded &frewards.yml&#FFAAAA!"));

        CalendarUtils.reload();
        MenuManager.reload();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055╚ &#FF0055Successful reload!"));
        MESSAGEUTILS.sendLang(sender, "reload.success");
    }
}
