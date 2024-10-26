package com.artillexstudios.axcalendar.database.impl;

import com.artillexstudios.axcalendar.AxCalendar;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends Base {
    private Connection conn;

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(String.format("jdbc:sqlite:%s/data.db", AxCalendar.getInstance().getDataFolder()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getType() {
        return "SQLite";
    }

    @Override
    public void setup() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/data.db", AxCalendar.getInstance().getDataFolder()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        super.setup();
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
