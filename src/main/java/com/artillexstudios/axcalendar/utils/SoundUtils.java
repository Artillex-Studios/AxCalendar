package com.artillexstudios.axcalendar.utils;

import com.artillexstudios.axapi.utils.StringUtils;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static com.artillexstudios.axcalendar.AxCalendar.BUKKITAUDIENCES;

public class SoundUtils {
    private static final Pattern SPLIT = Pattern.compile("\\|");

    // entity_player_levelup|0.5|0.2
    public static void playSound(@NotNull Player player, @Nullable String sound) {
        if (sound == null) return;
        if (sound.isBlank()) return;

        try {
            float volume = 1f;
            float pitch = 1f;
            String[] split = SPLIT.split(sound);
            if (split.length == 3) {
                volume = Float.parseFloat(split[1]);
                pitch = Float.parseFloat(split[2]);
            }

            Key key = Key.key(split[0]);
            Sound s = Sound.sound()
                    .pitch(pitch)
                    .volume(volume)
                    .type(key)
                    .build();

            BUKKITAUDIENCES.player(player).playSound(s);
        } catch (InvalidKeyException ex) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxCalendar] The sound %sound% does not exist, this is a configuration issue!".replace("%sound%", sound)));
        }
    }
}
