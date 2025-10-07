package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axcalendar.gui.impl.CalendarGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Open {
    INSTANCE;

    public void execute(@NotNull Player player) {
        new CalendarGui(player).open();
    }
}
