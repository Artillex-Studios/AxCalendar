package com.artillexstudios.axcalendar.gui.data;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcalendar.gui.GuiFrame;
import com.artillexstudios.axcalendar.gui.impl.CalendarGui;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static com.artillexstudios.axcalendar.AxCalendar.MENU;
import static com.artillexstudios.axcalendar.AxCalendar.REWARDS;

public class MenuManager {
    private static final ConcurrentHashMap<Integer, Day> days = new ConcurrentHashMap<>();

    public static void reload() {
        for (CalendarGui gui : CalendarGui.getOpenMenus()) {
            gui.getGui().close(gui.getPlayer());
        }

        days.clear();
        for (String route : MENU.getBackingDocument().getRoutesAsStrings(false)) {
            Section s = MENU.getSection(route);
            if (s == null) continue;

            IntArrayList dayNum = GuiFrame.getSlots(s.getStringList("days"));
            if (dayNum.isEmpty()) continue;

            Section claimable = s.getSection("item-claimable");
            Section claimed = s.getSection("item-claimed");
            Section unclaimable = s.getSection("item-unclaimable");
            Section expired = s.getSection("item-expired");

            var slots = GuiFrame.getSlots(s.getStringList("slot"));

            int idx = 0;
            for (Integer i : dayNum) {
                Day day = new Day(i,
                        ItemBuilder.create(claimable).get(),
                        ItemBuilder.create(claimed).get(),
                        ItemBuilder.create(unclaimable).get(),
                        ItemBuilder.create(expired).get(),
                        slots.getInt(idx),
                        new ArrayList<>()
                );
                days.put(i, day);
                idx++;
            }
        }


        for (String route : REWARDS.getBackingDocument().getRoutesAsStrings(false)) {
            Section s = REWARDS.getSection(route);
            if (s == null) continue;

            IntArrayList dayNum = GuiFrame.getSlots(s.getStringList("days"));
            if (dayNum.isEmpty()) continue;

            Reward reward = new Reward(
                    route,
                    dayNum,
                    s.getStringList("commands"),
                    s.getMapList("items"),
                    s.getString("message")
            );

            for (Integer i : dayNum) {
                Day day = days.get(i);
                if (day == null) continue;
                day.rewards().add(reward);
            }
        }
    }

    public static ConcurrentHashMap<Integer, Day> getDays() {
        return days;
    }
}
