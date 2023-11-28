package com.artillexstudios.axcalendar.database.impl;

import com.artillexstudios.axcalendar.database.Database;
import com.artillexstudios.axcalendar.utils.IpUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class MySQL implements Database {
    private final HikariConfig hConfig = new HikariConfig();
    private HikariDataSource dataSource;

    @Override
    public String getType() {
        return "MySQL";
    }

    @Override
    public void setup() {

        hConfig.setPoolName("axboosters-pool");

        hConfig.setMaximumPoolSize(CONFIG.getInt("database.pool.maximum-pool-size"));
        hConfig.setMinimumIdle(CONFIG.getInt("database.pool.minimum-idle"));
        hConfig.setMaxLifetime(CONFIG.getInt("database.pool.maximum-lifetime"));
        hConfig.setKeepaliveTime(CONFIG.getInt("database.pool.keepalive-time"));
        hConfig.setConnectionTimeout(CONFIG.getInt("database.pool.connection-timeout"));

        hConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hConfig.setJdbcUrl("jdbc:mysql://" + CONFIG.getString("database.address") + ":"+ CONFIG.getString("database.port") +"/" + CONFIG.getString("database.database"));
        hConfig.addDataSourceProperty("user", CONFIG.getString("database.username"));
        hConfig.addDataSourceProperty("password", CONFIG.getString("database.password"));

        dataSource = new HikariDataSource(hConfig);

        final String CREATE_TABLE = """
                        CREATE TABLE IF NOT EXISTS axcalendar_data (
                        	`uuid` VARCHAR(36) NOT NULL,
                        	`day` INT(64) NOT NULL,
                        	`ipv4` INT UNSIGNED NOT NULL
                        );
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void claim(@NotNull Player player, int day) {

        final String sql = """
                        INSERT INTO axcalendar_data (uuid, day, ipv4) VALUES (?, ?, ?);
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setInt(2, day);
            stmt.setInt(3, IpUtils.ipToInt(player.getAddress().getAddress()));

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isClaimed(@NotNull Player player, int day) {

        final String sql = """
                        SELECT * FROM axcalendar_data WHERE uuid = ? AND day = ? LIMIT 1;
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setInt(2, day);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public ArrayList<Integer> claimedDays(@NotNull Player player) {
        final ArrayList<Integer> claimedDays = new ArrayList<>();

        final String sql = """
                        SELECT day FROM axcalendar_data WHERE uuid = ?;
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) claimedDays.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return claimedDays;
    }

    @Override
    public int countIps(@NotNull Player player, int day) {

        final String sql = """
                        SELECT COUNT(*) FROM axcalendar_data WHERE ipv4 = ? AND day = ?;
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, IpUtils.ipToInt(player.getAddress().getAddress()));
            stmt.setInt(2, day);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
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
