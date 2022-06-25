package com.conutik.economy.utils;

import com.conutik.economy.Eco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private final Connection connection = Eco.getSQLInstance();
    private final String table;

    public Database(String table) {
        this.table = table;
        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(id VARCHAR(50) PRIMARY KEY, balance DOUBLE(32, 2) NOT NULL);").executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet findOne(String key, String query) throws SQLException {
        String statement = "SELECT * FROM " + this.table + " WHERE " + key + "=\"" + query + "\"";
        ResultSet result = connection.prepareStatement(statement).executeQuery();
        if(!result.next()) return null;
        return result;
    }

    public boolean updateOne(String id, Double balance) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO economy (id, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance=?");
        statement.setString(1, id);
        statement.setDouble(2, balance);
        statement.setDouble(3, balance);
        int result = statement.executeUpdate();
        return true;
    }

    public int subtractOne(String id, Double amount) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO economy (id, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = balance - ?");
        statement.setString(1, id);
        statement.setDouble(2, amount);
        statement.setDouble(3, amount);
        return statement.executeUpdate();
    }

    public int addOne(String key, Double amount) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO economy (id, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = balance + ?");
        statement.setString(1, key);
        statement.setDouble(2, amount);
        statement.setDouble(3, amount);
        return statement.executeUpdate();
    }
}
