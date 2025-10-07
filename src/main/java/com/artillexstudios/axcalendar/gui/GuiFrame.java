package com.artillexstudios.axcalendar.gui;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcalendar.utils.ItemBuilderUtil;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiFrame {
    protected final Config file;
    protected BaseGui gui;
    protected Player player;

    public BaseGui getGui() {
        return gui;
    }

    public GuiFrame(Config file, Player player) {
        this.file = file;
        this.player = player;
    }

    public static IntArrayList getSlots(List<String> s) {
        final IntArrayList slots = new IntArrayList();

        for (String str : s) {
            if (NumberUtils.isInt(str)) {
                slots.add(Integer.parseInt(str));
            } else {
                String[] split = str.split("-");
                int min = Integer.parseInt(split[0]);
                int max = Integer.parseInt(split[1]);
                for (int i = min; i <= max; i++) {
                    slots.add(i);
                }
            }
        }

        return slots;
    }

    public void setGui(BaseGui gui) {
        this.gui = gui;
        for (String str : file.getBackingDocument().getRoutesAsStrings(false)) createItem(str);
    }

    @NotNull
    public Config getFile() {
        return file;
    }

    protected ItemStack buildItem(@NotNull String key) {
        return buildItem(key, Map.of());
    }

    protected ItemStack buildItem(@NotNull String key, Map<String, String> replacements) {
        final Section section = file.getSection(key);
        final ItemStack item = ItemBuilderUtil.newBuilder(section, replacements).get();
        if (section.getOptionalString("texture").isEmpty() && item.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            item.setItemMeta(skullMeta);
        }
        return item;
    }

    protected void createItem(@NotNull String route) {
        createItem(route, event -> {}, Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action) {
        createItem(route, action, Map.of());
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        if (file.getString(route + ".slot") == null && file.getStringList(route + ".slot").isEmpty()) return;
        if (file.getString(route + ".material") == null && file.getString(route + ".type") == null) return;
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        createItem(route, action, replacements, getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots));
    }

    protected void createItem(@NotNull String route, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements, IntArrayList slots) {
        final GuiItem guiItem = new GuiItem(buildItem(route, replacements), action);
        gui.setItem(slots, guiItem);
    }

    protected void createItem(@NotNull String route, @NotNull ItemStack item, @Nullable GuiAction<InventoryClickEvent> action) {
        if (file.getSection(route) == null) return;
        final GuiItem guiItem = new GuiItem(item, action);
        final List<String> slots = file.getBackingDocument().getStringList(route + ".slot");
        gui.setItem(getSlots(slots.isEmpty() ? List.of(file.getString(route + ".slot")) : slots), guiItem);
    }

    protected void extendLore(ItemStack item, String... lore) {
        final ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();
        if (meta.getLore() != null) newLore.addAll(meta.getLore());
        newLore.addAll(StringUtils.formatListToString(Arrays.asList(lore)));
        meta.setLore(newLore);
        item.setItemMeta(meta);
    }

    public void updateTitle() {
    }
}
