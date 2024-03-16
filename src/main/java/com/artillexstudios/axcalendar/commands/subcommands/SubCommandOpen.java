package com.artillexstudios.axcalendar.commands.subcommands;

import com.artillexstudios.axcalendar.gui.impl.CalendarGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SubCommandOpen {

    public void subCommand(@NotNull Player player) {
        new CalendarGui(player).open();
    }
}
