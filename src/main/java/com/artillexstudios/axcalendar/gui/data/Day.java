package com.artillexstudios.axcalendar.gui.data;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Day(int day, ItemStack claimable, ItemStack claimed, ItemStack unclaimable, ItemStack expired,
                  int slot, List<Reward> rewards) {
}
