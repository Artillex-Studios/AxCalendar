package com.artillexstudios.axcalendar.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

import static com.artillexstudios.axcalendar.AxCalendar.CONFIG;

public class MySQL extends Base {
    private final HikariConfig hConfig = new HikariConfig();
    private HikariDataSource dataSource;

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String getType() {
        return "MySQL";
    }

    @Override
    public void setup() {

        hConfig.setPoolName("axrewards-pool");

        hConfig.setMaximumPoolSize(CONFIG.getInt("database.pool.maximum-pool-size"));
        hConfig.setMinimumIdle(CONFIG.getInt("database.pool.minimum-idle"));
        hConfig.setMaxLifetime(CONFIG.getInt("database.pool.maximum-lifetime"));
        hConfig.setKeepaliveTime(CONFIG.getInt("database.pool.keepalive-time"));
        hConfig.setConnectionTimeout(CONFIG.getInt("database.pool.connection-timeout"));

        hConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hConfig.setJdbcUrl("jdbc:mysql://" + CONFIG.getString("database.address") + ":"+ CONFIG.getString("database.port") +"/" + CONFIG.getString("database.database"));
        hConfig.addDataSourceProperty("user", CONFIG.getString("database.username"));
        hConfig.addDataSourceProperty("password", CONFIG.getString("database.password"));

        dataSource = new HikariDataSource(hConfig);

        super.setup();
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
