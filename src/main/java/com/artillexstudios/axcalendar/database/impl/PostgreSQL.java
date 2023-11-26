package com.artillexstudios.axcalendar.database.impl;

import com.artillexstudios.axboosters.booster.Booster;
import com.artillexstudios.axboosters.database.Database;
import com.artillexstudios.axboosters.enums.Audience;
import com.artillexstudios.axcalendar.database.Database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static com.artillexstudios.axboosters.AxBoosters.CONFIG;
import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class PostgreSQL implements Database {
    private final HikariConfig hConfig = new HikariConfig();
    private HikariDataSource dataSource;

    @Override
    public String getType() {
        return "PostgreSQL";
    }

    @Override
    public void setup() {

        hConfig.setPoolName("axboosters-pool");

        hConfig.setMaximumPoolSize(CONFIG.getInt("database.pool.maximum-pool-size"));
        hConfig.setMinimumIdle(CONFIG.getInt("database.pool.minimum-idle"));
        hConfig.setMaxLifetime(CONFIG.getInt("database.pool.maximum-lifetime"));
        hConfig.setKeepaliveTime(CONFIG.getInt("database.pool.keepalive-time"));
        hConfig.setConnectionTimeout(CONFIG.getInt("database.pool.connection-timeout"));

        hConfig.setDriverClassName("org.postgresql.Driver");
        hConfig.setJdbcUrl("jdbc:postgresql://" + CONFIG.getString("database.address") + ":"+ CONFIG.getString("database.port") +"/" + CONFIG.getString("database.database"));
        hConfig.addDataSourceProperty("user", CONFIG.getString("database.username"));
        hConfig.addDataSourceProperty("password", CONFIG.getString("database.password"));

        dataSource = new HikariDataSource(hConfig);

        final String CREATE_TABLE = """
                        CREATE TABLE axcalendar_data (
                        	`uuid` VARCHAR(36) NOT NULL,
                        	`day` INT(64) NOT NULL
                        );
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void claim(@NotNull UUID uuid, int day) {

        final String sql = """
                        INSERT INTO axcalendar_data (uuid, day) VALUES (?, ?);
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, day);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isClaimed(@NotNull UUID uuid, int day) {

        final String sql = """
                        SELECT * FROM axcalendar_data WHERE uuid = ? AND day = ? LIMIT 1;
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public ArrayList<Integer> claimedDays(@NotNull UUID uuid) {
        final ArrayList<Integer> claimedDays = new ArrayList<>();

        final String sql = """
                        SELECT day FROM axcalendar_data WHERE uuid = ?;
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) claimedDays.add(rs.getInt(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return claimedDays;
    }

    @Override
    public void disable() {
        try {
            dataSource.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
