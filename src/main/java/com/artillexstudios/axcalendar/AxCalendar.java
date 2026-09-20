package com.artillexstudios.axcalendar;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.dependencies.DependencyManagerWrapper;
import com.artillexstudios.axapi.executor.ThreadedQueue;
import com.artillexstudios.axapi.libs.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.metrics.AxMetrics;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axapi.utils.logging.LoggerNameFormat;
import com.artillexstudios.axcalendar.commands.Commands;
import com.artillexstudios.axcalendar.database.Database;
import com.artillexstudios.axcalendar.database.impl.H2;
import com.artillexstudios.axcalendar.database.impl.MySQL;
import com.artillexstudios.axcalendar.database.impl.PostgreSQL;
import com.artillexstudios.axcalendar.database.impl.SQLite;
import com.artillexstudios.axcalendar.gui.GuiUpdater;
import com.artillexstudios.axcalendar.gui.data.MenuManager;
import com.artillexstudios.axcalendar.hooks.PlaceholderAPIParser;
import com.artillexstudios.axcalendar.hooks.Placeholders;
import com.artillexstudios.axcalendar.libraries.Libraries;
import com.artillexstudios.axcalendar.utils.CalendarUtils;
import com.artillexstudios.axcalendar.utils.UpdateNotifier;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

import java.io.File;

public final class AxCalendar extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static Config MENU;
    public static Config REWARDS;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;
    private static ThreadedQueue<Runnable> threadedQueue;
    private static Database database;
    private static Placeholders placeholderParser;
    private static AxMetrics metrics;

    public static ThreadedQueue<Runnable> getThreadedQueue() {
        return threadedQueue;
    }

    public static Database getDatabase() {
        return database;
    }

    public static AxPlugin getInstance() {
        return instance;
    }

    public static Placeholders getPlaceholderParser() {
        return placeholderParser;
    }

    @Override
    public void dependencies(DependencyManagerWrapper manager) {
        instance = this;
        Libraries.load(instance, manager);
    }

    @Override
    public void enable() {
        new Metrics(this, 20392);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        MENU = new Config(new File(getDataFolder(), "menu.yml"), getResource("menu.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().build(), DumperSettings.DEFAULT, UpdaterSettings.builder().build());
        REWARDS = new Config(new File(getDataFolder(), "rewards.yml"), getResource("rewards.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().build(), DumperSettings.DEFAULT, UpdaterSettings.builder().build());

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        threadedQueue = new ThreadedQueue<>("AxCalendar-Datastore-thread");

        Commands.registerCommand();
        CalendarUtils.reload();
        MenuManager.reload();

        switch (CONFIG.getString("database.type").toLowerCase()) {
            case "sqlite" -> database = new SQLite();
            case "mysql" -> database = new MySQL();
            case "postgresql" -> database = new PostgreSQL();
            default -> database = new H2();
        }

        database.setup();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderParser = new PlaceholderAPIParser();
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055[AxCalendar] Hooked into PlaceholderAPI!"));
        } else {
            placeholderParser = new Placeholders() {};
        }

        GuiUpdater.start();

        metrics = new AxMetrics(this, 21);
        metrics.start();

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0055[AxCalendar] Loaded plugin! Using &f" + database.getType() + " &#FF0055database to store data!"));

        UpdateNotifier.init(CONFIG, LANG);
        if (CONFIG.getBoolean("update-notifier.enabled", true)) new UpdateNotifier();
    }

    @Override
    public void disable() {
        if (metrics != null) metrics.cancel();
        database.disable();
        GuiUpdater.stop();
    }

    @Override
    public void updateFlags() {
        FeatureFlags.LOGGER_NAME_FORMAT.set(LoggerNameFormat.NAMEABLE);
    }
}
