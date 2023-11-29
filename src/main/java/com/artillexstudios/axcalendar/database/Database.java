package com.artillexstudios.axcalendar.database;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    void claim(@NotNull Player player, int day);

    boolean isClaimed(@NotNull Player player, int day);

    ArrayList<Integer> claimedDays(@NotNull Player player);

    int countIps(@NotNull Player player, int day);

    void reset(@NotNull UUID uuid);

    void disable();
}
