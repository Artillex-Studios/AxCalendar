package com.artillexstudios.axcalendar.gui.data;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.List;
import java.util.Map;

public record Reward(String name, IntArrayList days,
                     List<String> claimCommands, List<Map<?, ?>> claimItems,
                     String message
) {
}
