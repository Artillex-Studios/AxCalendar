package com.artillexstudios.axcalendar.gui.impl;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.gui.GuiFrame;
import com.artillexstudios.axcalendar.utils.ActionUtils;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import com.artillexstudios.axcalendar.utils.RequirementUtils;
import com.artillexstudios.axcalendar.utils.TimeUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.WeakHashMap;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGES;
import static com.artillexstudios.axcalendar.AxCalendar.MESSAGEUTILS;

public class CalendarGui extends GuiFrame {
    private static final WeakHashMap<CalendarGui, Void> map = new WeakHashMap<>();
    private boolean opened = false;
    final Gui gui = Gui.gui()
            .title(StringUtils.format(MESSAGES.getString("menu.title")))
            .rows(MESSAGES.getInt("menu.rows", 6))
            .disableAllInteractions()
            .create();

    public CalendarGui(Player player) {
        super(MESSAGES.getSection("menu"), player);
        setGui(gui);
        map.put(this, null);
    }

    public void open() {
        if (MESSAGES.getSection("menu.filler") != null)
            gui.getFiller().fill(new GuiItem(new ItemBuilder(MESSAGES.getSection("menu.filler")).get()));

        if (MESSAGES.getSection("menu.other") != null) {
            for (String str : MESSAGES.getSection("menu.other").getRoutesAsStrings(false)) {
                final GuiItem guiItem = new GuiItem(new ItemBuilder(MESSAGES.getSection("menu.other." + str + ".item")).get());
                guiItem.setAction(event -> {
                    for (String str2 : MESSAGES.getStringList("menu.other." + str + ".actions")) {
                        ActionUtils.handleAction(event.getWhoClicked(), str2, -1);
                    }
                });
                gui.setItem(MESSAGES.getInt("menu.other." + str + ".slot"), guiItem);
            }
        }

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
            final ItemStack item = new ItemBuilder(section).setName(section.getString("name"), replacements).setLore(section.getStringList("lore"), replacements).get();
            item.setAmount(MESSAGES.getInt("menu.days." + str + ".amount", 1));
            final GuiItem guiItem = new GuiItem(item);
            gui.setItem(MESSAGES.getInt("menu.days." + str + ".slot"), guiItem);

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

                int maxIP = CONFIG.getInt("max-accounts-per-ip", 3);
                if (maxIP != -1 && maxIP <= AxCalendar.getDatabase().countIps(player, day)) {
                    MESSAGEUTILS.sendLang(player, "error.too-many-ips");
                    return;
                }

                AxCalendar.getDatabase().claim(player, day);

                open();

                for (String str2 : MESSAGES.getStringList("menu.days." + str + ".actions")) {
                    ActionUtils.handleAction(event.getWhoClicked(), str2, day);
                }
            });
        }

        if (opened) {
            gui.update();
            return;
        }
        opened = true;

        final CalendarGui calendarGui = this;
        gui.setCloseGuiAction(inventoryCloseEvent -> map.remove(calendarGui));

        Scheduler.get().run(scheduledTask -> gui.open(player));
    }

    public static WeakHashMap<CalendarGui, Void> getMap() {
        return map;
    }
}
