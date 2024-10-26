package com.artillexstudios.axcalendar.database;

import com.artillexstudios.axcalendar.gui.data.Day;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    void claim(@NotNull Player player, Day day);

    boolean isClaimed(@NotNull Player player, Day day);

    int countIps(@NotNull Player player, Day day);

    void reset(@NotNull OfflinePlayer player);

    void disable();
}
