package com.artillexstudios.axcalendar.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class ItemBuilderUtil {

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder newBuilder(@NotNull Section section) {
        return newBuilder(section, Map.of());
    }

    @NotNull
    public static ItemBuilder newBuilder(@NotNull Section section, Map<String, String> replacements) {
        final ItemBuilder builder = ItemBuilder.create(section);

        section.getOptionalString("name").ifPresent((name) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                name = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, name);
            }
            builder.setName(name, replacements);
        });

        section.getOptionalStringList("lore").ifPresent((lore) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                lore = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, lore);
            }
            builder.setLore(lore, replacements);
        });

        return builder;
    }

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder newBuilder(@NotNull ItemStack itemStack) {
        return ItemBuilder.create(itemStack);
    }

    public static ItemStack parse(@NotNull ItemStack item, Map<String, String> replacements) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        Optional.of(meta.getDisplayName()).ifPresent((name) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                name = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, name);
            }
            for (Map.Entry<String, String> e : replacements.entrySet()) {
                name = name.replace(e.getKey(), e.getValue());
            }
            meta.setDisplayName(name);
        });

        Optional.ofNullable(meta.getLore()).ifPresent((lore) -> {
            if (ClassUtils.INSTANCE.classExists("me.clip.placeholderapi.PlaceholderAPI")) {
                lore = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, lore);
            }
            for (Map.Entry<String, String> e : replacements.entrySet()) {
                for (int i = 0; i < lore.size(); i++) {
                    lore.set(i, lore.get(i).replace(e.getKey(), e.getValue()));
                }
            }
            meta.setLore(lore);
        });

        item.setItemMeta(meta);
        return item;
    }
}
