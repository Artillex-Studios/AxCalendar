package com.artillexstudios.axcalendar.gui;

import com.artillexstudios.axcalendar.gui.impl.CalendarGui;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class GuiUpdater {

    public void start() {
        if (CONFIG.getInt("update-gui", 20) == -1) return;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> CalendarGui.getMap().forEach((key, value) -> key.open()), 1, CONFIG.getInt("update-gui", 20) / 20, TimeUnit.SECONDS);
    }
}
