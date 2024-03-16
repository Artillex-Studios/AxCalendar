package com.artillexstudios.axcalendar.gui;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axcalendar.utils.ItemBuilderUtil;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class GuiFrame {
    protected final Section file;
    protected BaseGui gui;
    protected Player player;

    public GuiFrame(Section file, Player player) {
        this.file = file;
        this.player = player;
    }

    public void setGui(BaseGui gui) {
        this.gui = gui;
        for (String str : file.getRoutesAsStrings(false)) createItem(str, str);
    }

    protected ItemStack buildItem(@NotNull String key) {
        return ItemBuilderUtil.newBuilder(file.getSection(key)).get();
    }

    protected ItemStack buildItem(@NotNull String key, Map<String, String> replacements) {
        return ItemBuilderUtil.newBuilder(file.getSection(key), replacements).get();
    }

    protected void createItem(@NotNull String route, String prevRoute) {
        createItem(route, prevRoute, null, Map.of());
    }

    protected void createItem(@NotNull String route, String prevRoute, @Nullable GuiAction<InventoryClickEvent> action) {
        createItem(route, prevRoute, action, Map.of());
    }

    protected void createItem(@NotNull String route, String prevRoute, @Nullable GuiAction<InventoryClickEvent> action, Map<String, String> replacements) {
        if (file.getString(route + ".type") == null && file.getString(route + ".material") == null) return;
        final GuiItem guiItem = new GuiItem(buildItem(route, replacements), action);
        final List<Integer> slots = file.getIntList(prevRoute + ".slot");
        if (slots.isEmpty()) gui.setItem(file.getInt(prevRoute + ".slot"), guiItem);
        else gui.setItem(slots, guiItem);
    }
}
