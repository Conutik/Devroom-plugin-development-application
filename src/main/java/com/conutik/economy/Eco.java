package com.conutik.economy;

import com.conutik.economy.commands.Money;
import com.conutik.economy.utils.CustomEconomy;
import com.conutik.economy.utils.Database;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Eco extends JavaPlugin {

    private static Connection connection;
    private static Economy econ;
    private static Database db;

    public static Connection getSQLInstance() {
        return connection;
    }

    public static Database getDatabase() {
        return db;
    }
    public static Economy getEconomyInstance() {
        return econ;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!connectToDB() || !setupEconomy()) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        getCommand("money").setExecutor(new Money());
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Economy has been enabled");
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Economy has been disabled");
    }

    public boolean connectToDB() {
        try {
            FileConfiguration config = getConfig();
            connection = DriverManager.getConnection(config.getString("url"), config.getString("username"), config.getString("password"));
            db = new Database("economy");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not connect to the database, disabling the plugin");
            return false;
        }
        return true;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        econ = new CustomEconomy();

        this.getServer().getServicesManager().register(Economy.class, econ, this.getServer().getPluginManager().getPlugin("Vault"), ServicePriority.Normal);

        return true;
    }

}
