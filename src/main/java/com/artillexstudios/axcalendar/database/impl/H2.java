package com.artillexstudios.axcalendar.database.impl;

import com.artillexstudios.axcalendar.AxCalendar;
import com.artillexstudios.axcalendar.database.Database;
import com.artillexstudios.axcalendar.utils.IpUtils;
import org.bukkit.entity.Player;
import org.h2.jdbc.JdbcConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

public class H2 implements Database {
    private Connection conn;

    @Override
    public String getType() {
        return "H2";
    }

    @Override
    public void setup() {

        try {
            conn = new JdbcConnection("jdbc:h2:./" + AxCalendar.getInstance().getDataFolder() + "/data;mode=MySQL", new Properties(), null, null, false);
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final String CREATE_TABLE = """
                        CREATE TABLE IF NOT EXISTS axcalendar_data (
                        	`uuid` VARCHAR(36) NOT NULL,
                        	`day` INT(64) NOT NULL,
                        	`ipv4` INT UNSIGNED NOT NULL
                        );
                """;

        try (PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void claim(@NotNull Player player, int day) {

        final String sql = """
                        INSERT INTO axcalendar_data (uuid, `day`, ipv4) VALUES (?, ?, ?);
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                        SELECT * FROM axcalendar_data WHERE (`uuid` = ? AND `day` = ?) LIMIT 1;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                        SELECT `day` FROM axcalendar_data WHERE uuid = ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                        SELECT COUNT(*) FROM axcalendar_data WHERE ipv4 = ? AND `day` = ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    public void reset(@NotNull UUID uuid) {

        final String sql = """
                        DELETE FROM axcalendar_data WHERE uuid = ?;
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void disable() {
        try {
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
