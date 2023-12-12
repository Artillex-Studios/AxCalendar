package com.artillexstudios.axcalendar.utils;

import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.utils.StringUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta meta;
    private final TagResolver[] resolvers;

    public ItemBuilder(@NotNull Section section) {
        String type = section.getString("type");
        if (type == null) {
            type = section.getString("material", "stone");
        }

        this.itemStack = new ItemStack(getMaterial(type));
        if (itemStack.getType().equals(Material.AIR) || itemStack.getType().equals(Material.CAVE_AIR) || itemStack.getType().equals(Material.VOID_AIR) || itemStack.getType().equals(Material.LEGACY_AIR)) {
            this.meta = null;
        } else {
            this.meta = itemStack.getItemMeta();
        }

        this.resolvers = new TagResolver[]{TagResolver.resolver()};
        if (this.meta != null) {
            section.getOptionalString("texture").ifPresent(this::setTextureValue);
            section.getOptionalString("name").ifPresent((name) -> {
                this.setName(name, this.resolvers);
            });
            section.getOptionalString("color").ifPresent(this::setColor);
            section.getOptionalInt("custom-model-data").ifPresent(this::setCustomModelData);
            section.getOptionalInt("amount").ifPresent(this::amount);
            section.getOptionalBoolean("glow").ifPresent(this::glow);
            section.getOptionalStringList("lore").ifPresent((lore) -> {
                this.setLore(lore, this.resolvers);
            });
            section.getOptionalStringList("enchants").ifPresent((enchants) -> {
                this.addEnchants(createEnchantmentsMap(enchants));
            });
            section.getOptionalStringList("item-flags").ifPresent((flags) -> {
                this.applyItemFlags(getItemFlags(flags));
            });
        }
    }

    public ItemBuilder(@NotNull Section section, TagResolver... resolvers) {
        String type = section.getString("type");
        if (type == null) {
            type = section.getString("material", "stone");
        }

        this.itemStack = new ItemStack(getMaterial(type));
        if (itemStack.getType().equals(Material.AIR) || itemStack.getType().equals(Material.CAVE_AIR) || itemStack.getType().equals(Material.VOID_AIR) || itemStack.getType().equals(Material.LEGACY_AIR)) {
            this.meta = null;
        } else {
            this.meta = itemStack.getItemMeta();
        }

        this.resolvers = resolvers;
        if (this.meta != null) {
            section.getOptionalString("texture").ifPresent(this::setTextureValue);
            section.getOptionalString("name").ifPresent((name) -> {
                this.setName(name, resolvers);
            });
            section.getOptionalString("color").ifPresent(this::setColor);
            section.getOptionalInt("custom-model-data").ifPresent(this::setCustomModelData);
            section.getOptionalInt("amount").ifPresent(this::amount);
            section.getOptionalBoolean("glow").ifPresent(this::glow);
            section.getOptionalStringList("lore").ifPresent((lore) -> {
                this.setLore(lore, resolvers);
            });
            section.getOptionalStringList("enchants").ifPresent((enchants) -> {
                this.addEnchants(createEnchantmentsMap(enchants));
            });
            section.getOptionalStringList("item-flags").ifPresent((flags) -> {
                this.applyItemFlags(getItemFlags(flags));
            });
        }
    }

    public ItemBuilder(Map<Object, Object> map, TagResolver... resolvers) {
        String type = (String) map.get("type");
        if (type == null) {
            type = (String) map.getOrDefault("material", "stone");
        }

        this.itemStack = new ItemStack(getMaterial(type));
        if (itemStack.getType().equals(Material.AIR) || itemStack.getType().equals(Material.CAVE_AIR) || itemStack.getType().equals(Material.VOID_AIR) || itemStack.getType().equals(Material.LEGACY_AIR)) {
            this.meta = null;
        } else {
            this.meta = itemStack.getItemMeta();
        }

        this.resolvers = resolvers;
        if (this.meta != null) {
            Optional.ofNullable(map.get("texture")).ifPresent((string) -> {
                this.setTextureValue((String) string);
            });
            Optional.ofNullable(map.get("name")).ifPresent((name) -> {
                this.setName((String) name, resolvers);
            });
            Optional.ofNullable(map.get("color")).ifPresent((color) -> {
                this.setColor((String) color);
            });
            Optional.ofNullable(map.get("custom-model-data")).ifPresent((number) -> {
                this.setCustomModelData((Integer) number);
            });
            Optional.ofNullable(map.get("amount")).ifPresent((amount) -> {
                this.itemStack.setAmount((Integer) amount);
            });
            Optional.ofNullable(map.get("glow")).ifPresent((glow) -> {
                this.glow((Boolean) glow);
            });
            Optional.ofNullable(map.get("lore")).ifPresent((lore) -> {
                this.setLore((List) lore, resolvers);
            });
            Optional.ofNullable(map.get("enchants")).ifPresent((enchants) -> {
                this.addEnchants(createEnchantmentsMap((List) enchants));
            });
            Optional.ofNullable(map.get("item-flags")).ifPresent((flags) -> {
                this.applyItemFlags(getItemFlags((List) flags));
            });
        }
    }

    public ItemBuilder(@NotNull Material material) {
        this.itemStack = new ItemStack(material);
        if (itemStack.getType().equals(Material.AIR) || itemStack.getType().equals(Material.CAVE_AIR) || itemStack.getType().equals(Material.VOID_AIR) || itemStack.getType().equals(Material.LEGACY_AIR)) {
            this.meta = null;
        } else {
            this.meta = itemStack.getItemMeta();
        }

        this.resolvers = new TagResolver[]{TagResolver.resolver()};
    }

    public ItemBuilder(@NotNull ItemStack item) {
        this.itemStack = item;
        if (itemStack.getType().equals(Material.AIR) || itemStack.getType().equals(Material.CAVE_AIR) || itemStack.getType().equals(Material.VOID_AIR) || itemStack.getType().equals(Material.LEGACY_AIR)) {
            this.meta = null;
        } else {
            this.meta = itemStack.getItemMeta();
        }

        this.resolvers = new TagResolver[]{TagResolver.resolver()};
    }

    private static Material getMaterial(String name) {
        Material material = Material.matchMaterial(name.toUpperCase(Locale.ENGLISH));
        return material != null ? material : Material.BEDROCK;
    }

    @NotNull
    private static Map<Enchantment, Integer> createEnchantmentsMap(@NotNull List<String> enchantments) {
        Map<Enchantment, Integer> enchantsMap = new HashMap(enchantments.size());
        Iterator var2 = enchantments.iterator();

        while (true) {
            String[] enchant;
            Enchantment ench;
            do {
                if (!var2.hasNext()) {
                    return enchantsMap;
                }

                String enchantment = (String) var2.next();
                enchant = enchantment.split(":");
                ench = Enchantment.getByKey(NamespacedKey.minecraft(enchant[0]));
            } while (ench == null);

            int level;
            try {
                level = Integer.parseInt(enchant[1]);
            } catch (Exception var8) {
                continue;
            }

            enchantsMap.put(ench, level);
        }
    }

    @NotNull
    private static List<ItemFlag> getItemFlags(@NotNull List<String> flags) {
        List<ItemFlag> flagList = new ArrayList(flags.size());
        Iterator var2 = flags.iterator();

        while (var2.hasNext()) {
            String flag = (String) var2.next();

            ItemFlag itemFlag;
            try {
                itemFlag = ItemFlag.valueOf(flag.toUpperCase(Locale.ENGLISH));
            } catch (Exception var6) {
                continue;
            }

            flagList.add(itemFlag);
        }

        return flagList;
    }

    public ItemBuilder setName(String name) {
        this.setName(name, TagResolver.resolver());
        return this;
    }

//    public <T, Z> ItemBuilder storePersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
//        this.meta.getPersistentDataContainer().set(key, type, value);
//        return this;
//    }

    public ItemBuilder setName(String name, Map<String, String> replacements) {
        AtomicReference<String> toFormat = new AtomicReference(name);
        replacements.forEach((pattern, replacement) -> {
            toFormat.set(toFormat.get().replace(pattern, replacement));
        });
        this.setName(toFormat.get());
        return this;
    }

    public ItemBuilder setName(String name, TagResolver... resolvers) {
        this.meta.setDisplayName(StringUtils.formatToString(name, resolvers));
        return this;
    }

    public ItemBuilder setColor(String colorString) {
        if (this.meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) this.meta;
            String[] rgb = colorString.replace(" ", "").split(",");
            itemMeta.setColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
        }

        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            this.meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setCustomModelData(Integer modelData) {
        try {
            this.meta.setCustomModelData(modelData);
        } catch (NoSuchMethodError ignored) {
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.setLore(lore, TagResolver.resolver());
        return this;
    }

    public ItemBuilder setLore(List<String> lore, Map<String, String> replacements) {
        List<String> newList = new ArrayList(replacements.size());
        Iterator var4 = lore.iterator();

        while (var4.hasNext()) {
            String line = (String) var4.next();
            AtomicReference<String> toFormat = new AtomicReference(line);
            replacements.forEach((pattern, replacement) -> {
                toFormat.set(toFormat.get().replace(pattern, replacement));
            });
            newList.add(toFormat.get());
        }

        this.setLore(newList);
        return this;
    }

    public ItemBuilder setLore(List<String> lore, TagResolver... resolvers) {
        this.meta.setLore(StringUtils.formatListToString(lore, resolvers));
        return this;
    }

    public ItemBuilder setTextureValue(String texture) {
        try {
            NMSHandlers.getNmsHandler().setItemStackTexture(this.itemStack, texture);
        } catch (Exception ignored) {}
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchants(Map<Enchantment, Integer> enchantments) {
        this.itemStack.addEnchantments(enchantments);
        return this;
    }

    public ItemBuilder applyItemFlags(@NotNull List<ItemFlag> flags) {
        Iterator var2 = flags.iterator();

        while (var2.hasNext()) {
            ItemFlag flag = (ItemFlag) var2.next();
            this.meta.addItemFlags(flag);
        }

        return this;
    }

    public ItemStack get() {
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }

    public ItemStack clonedGet() {
        return this.get().clone();
    }
}