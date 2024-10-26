package com.artillexstudios.axcalendar.database.impl;

import com.artillexstudios.axcalendar.database.Database;
import com.artillexstudios.axcalendar.gui.data.Day;
import com.artillexstudios.axcalendar.utils.IpUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Base implements Database {
    private QueryRunner runner;

    private final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS `axcalendar_data` (
            	`uuid` VARCHAR(36) NOT NULL,
            	`day` INT NOT NULL,
            	`ipv4` INT NOT NULL,
            	PRIMARY KEY (`uuid`,`day`)
            );
    """;

    private final String CLAIM = """
            INSERT INTO axcalendar_data (`uuid`, `day`, `ipv4`) VALUES (?, ?, ?)
    """;

    private final String IS_CLAIMED = """
            SELECT count(*) FROM axcalendar_data WHERE `uuid` = ? AND `day` = ? LIMIT 1
    """;

    private final String COUNT_IP = """
            SELECT count(`ipv4`) FROM axcalendar_data WHERE `day` = ? AND `ipv4` = ?
    """;

    private final String DELETE_USER = """
            DELETE FROM axcalendar_data WHERE `uuid` = ?;
    """;

    public abstract Connection getConnection();

    @Override
    public abstract String getType();

    public QueryRunner getRunner() {
        return runner;
    }

    @Override
    public void setup() {
        runner = new QueryRunner();

        try (Connection conn = getConnection()) {
            runner.execute(conn, CREATE_TABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void claim(@NotNull Player player, Day day) {
        try (Connection conn = getConnection()) {
            runner.execute(conn, CLAIM, player.getUniqueId().toString(), day.day(), IpUtils.ipToInt(player));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isClaimed(@NotNull Player player, Day day) {
        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();
        try (Connection conn = getConnection()) {
            return runner.query(conn, IS_CLAIMED, scalarHandler, player.getUniqueId().toString(), day.day()) != 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public int countIps(@NotNull Player player, Day day) {
        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();
        try (Connection conn = getConnection()) {
            return Math.toIntExact(runner.query(conn, COUNT_IP, scalarHandler, day.day(), IpUtils.ipToInt(player)));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public void reset(@NotNull OfflinePlayer player) {
        try (Connection conn = getConnection()) {
            runner.execute(conn, DELETE_USER, player.getUniqueId().toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public abstract void disable();
}
