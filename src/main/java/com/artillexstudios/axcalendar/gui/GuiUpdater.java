package com.artillexstudios.axcalendar.gui;

import com.artillexstudios.axcalendar.gui.impl.CalendarGui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class GuiUpdater {
    private static ScheduledExecutorService service = null;

    public static void start() {
        if (service != null) service.shutdown();
        if (CONFIG.getInt("update-gui", 20) == -1) return;

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            try {
                for (CalendarGui gui : CalendarGui.getOpenMenus()) {
                    gui.open();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 1, CONFIG.getInt("update-gui", 20) / 20, TimeUnit.SECONDS);
    }

    public static void stop() {
        if (service == null) return;
        service.shutdown();
    }
}
