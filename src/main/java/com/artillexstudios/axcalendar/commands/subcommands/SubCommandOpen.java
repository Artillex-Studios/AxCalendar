package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axcalendar.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.utils.ActionUtils;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import com.artillexstudios.axcalendar.utils.RequirementUtils;
import com.artillexstudios.axcalendar.utils.TimeUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class SubCommandOpen {

    public void subCommand(@NotNull Player player) {

        final Gui menu = Gui.gui()
                .title(StringUtils.format(MESSAGES.getString("menu.title")))
                .rows(MESSAGES.getInt("menu.rows", 6))
                .disableAllInteractions()
                .create();

        if (MESSAGES.getSection("menu.filler") != null)
            menu.getFiller().fill(new GuiItem(new ItemBuilder(MESSAGES.getSection("menu.filler")).get()));

        if (MESSAGES.getSection("menu.other") != null) {
            for (String str : MESSAGES.getSection("menu.other").getRoutesAsStrings(false)) {
                final GuiItem guiItem = new GuiItem(new ItemBuilder(MESSAGES.getSection("menu.other." + str + ".item")).get());
                guiItem.setAction(event -> {
                    for (String str2 : MESSAGES.getStringList("menu.other." + str + ".actions")) {
                        ActionUtils.handleAction(event.getWhoClicked(), str2, -1);
                    }
                });
                menu.setItem(MESSAGES.getInt("menu.other." + str + ".slot"), guiItem);
            }
        }

        AxCalendar.getThreadedQueue().submit(() -> {
            for (String str : MESSAGES.getSection("menu.days").getRoutesAsStrings(false)) {
                int day = Integer.parseInt(str);
                boolean claimed = AxCalendar.getDatabase().isClaimed(player, day);
                int dayOfMonth = CalendarUtils.getDayOfMonth();
                String type;

                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("%day%", "" + day);
                replacements.put("%time%", TimeUtils.formatTime(CalendarUtils.getMilisUntilDay(day)));

                if (claimed) type = "claimed";
                else if (!CalendarUtils.isSameMonth() || dayOfMonth < day) type = "unclaimable";
                else type = "claimable";

                final Section section = MESSAGES.getSection("menu.days." + str + ".item-" + type);
                final GuiItem guiItem = new GuiItem(new ItemBuilder(section).setName(section.getString("name"), replacements).setLore(section.getStringList("lore"), replacements).get());
                menu.setItem(MESSAGES.getInt("menu.days." + str + ".slot"), guiItem);

                guiItem.setAction(event -> {

                    if (!RequirementUtils.canClaim(player)) {
                        MESSAGEUTILS.sendLang(player, "error.too-late");
                        return;
                    }

                    if (CalendarUtils.isSameMonth() && dayOfMonth > day && !CONFIG.getBoolean("allow-late-claiming", true)) {
                        MESSAGEUTILS.sendLang(player, "error.too-late");
                        return;
                    }

                    if (!CalendarUtils.isSameMonth() || dayOfMonth < day) {
                        MESSAGEUTILS.sendLang(player, "error.too-early");
                        return;
                    }

                    if (AxCalendar.getDatabase().isClaimed(player, day)) {
                        MESSAGEUTILS.sendLang(player, "error.already-claimed");
                        return;
                    }

                    if (CONFIG.getInt("max-accounts-per-ip", 3) <= AxCalendar.getDatabase().countIps(player, day)) {
                        MESSAGEUTILS.sendLang(player, "error.too-many-ips");
                        return;
                    }

                    AxCalendar.getDatabase().claim(player, day);

                    subCommand(player);

                    for (String str2 : MESSAGES.getStringList("menu.days." + str + ".actions")) {
                        ActionUtils.handleAction(event.getWhoClicked(), str2, day);
                    }
                });
            }

            Scheduler.get().run(scheduledTask -> menu.open(player));

            if (CONFIG.getInt("update-gui", 20) == -1) return;
            // TODO: rework this whole part
            Scheduler.get().runTimer(task -> {
                if (menu.getInventory().getViewers().isEmpty()) task.cancel();

                for (String str : MESSAGES.getSection("menu.days").getRoutesAsStrings(false)) {
                    int day = Integer.parseInt(str);
                    boolean claimed = AxCalendar.getDatabase().isClaimed(player, day);
                    int dayOfMonth = CalendarUtils.getDayOfMonth();
                    String type;

                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("%day%", "" + day);
                    replacements.put("%time%", TimeUtils.formatTime(CalendarUtils.getMilisUntilDay(day)));

                    if (claimed) type = "claimed";
                    else if (!CalendarUtils.isSameMonth() || dayOfMonth < day) type = "unclaimable";
                    else type = "claimable";

                    final Section section = MESSAGES.getSection("menu.days." + str + ".item-" + type);
                    final GuiItem guiItem = new GuiItem(new ItemBuilder(section).setName(section.getString("name"), replacements).setLore(section.getStringList("lore"), replacements).get());
                    menu.updateItem(MESSAGES.getInt("menu.days." + str + ".slot"), guiItem);

                    guiItem.setAction(event -> {

                        if (!RequirementUtils.canClaim(player)) {
                            MESSAGEUTILS.sendLang(player, "error.requirements-fail");
                            return;
                        }

                        if (CalendarUtils.isSameMonth() && dayOfMonth > day && !CONFIG.getBoolean("allow-late-claiming", true)) {
                            MESSAGEUTILS.sendLang(player, "error.too-late");
                            return;
                        }

                        if (!CalendarUtils.isSameMonth() || dayOfMonth < day) {
                            MESSAGEUTILS.sendLang(player, "error.too-early");
                            return;
                        }

                        if (AxCalendar.getDatabase().isClaimed(player, day)) {
                            MESSAGEUTILS.sendLang(player, "error.already-claimed");
                            return;
                        }

                        if (CONFIG.getInt("max-accounts-per-ip", 3) <= AxCalendar.getDatabase().countIps(player, day)) {
                            MESSAGEUTILS.sendLang(player, "error.too-many-ips");
                            return;
                        }

                        AxCalendar.getDatabase().claim(player, day);

                        subCommand(player);

                        for (String str2 : MESSAGES.getStringList("menu.days." + str + ".actions")) {
                            ActionUtils.handleAction(event.getWhoClicked(), str2, day);
                        }
                    });
                }
            }, 0, CONFIG.getInt("update-gui", 20));
        });

    }
}
