package com.artillexstudios.axcalendar.database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public interface Database {

    String getType();

    void setup();

    void claim(@NotNull UUID uuid, int day);

    boolean isClaimed(@NotNull UUID uuid, int day);

    ArrayList<Integer> claimedDays(@NotNull UUID uuid);

    void disable();
}
