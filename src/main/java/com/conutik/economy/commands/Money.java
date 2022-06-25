package com.conutik.economy.commands;

import com.conutik.economy.Eco;
import com.conutik.economy.utils.Database;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;

public class Money implements CommandExecutor {

    private final Economy econ = Eco.getEconomyInstance();
    private final Database db = Eco.getDatabase();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage("Only players can use this command :D");
            return true;
        }
        final Player player = (Player) sender;
        if(args.length <= 0) {
            player.sendMessage(ChatColor.GREEN + "Your balance: $" + econ.getBalance(player));
            return true;
        } else if(args[0].equalsIgnoreCase("pay")) {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "/money pay (user) (amount)");
                return true;
            }
            if(args[1] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the player to send money to");
                return true;
            }
            if(args[1].equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "You can't send yourself money D:");
                return true;
            }
            if(args[2] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the amount to send");
                return true;
            }
            if(Integer.parseInt(args[2]) <= 0) {
                player.sendMessage(ChatColor.RED + "Amount should be more than 0");
                return true;
            }
            double balance = econ.getBalance(player);
            if(balance <= Integer.parseInt(args[2])) {
                player.sendMessage(ChatColor.RED + "Insufficient funds");
                return true;
            }
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player is offline");
                return true;
            }
            econ.withdrawPlayer(player, Integer.parseInt(args[2]));
            econ.depositPlayer(Bukkit.getOfflinePlayer(args[1]), Integer.parseInt(args[2]));
            player.sendMessage(ChatColor.GREEN + "You have sent $" + Integer.parseInt(args[2]) + " to " + args[1]);
            Bukkit.getServer().getPlayer(args[1]).sendMessage(ChatColor.GREEN + " "  + player.getName() + "has sent you $" + Integer.parseInt(args[2]));
        }
        else if(args[0].equalsIgnoreCase("give")) {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "/money give (user) (amount)");
                return true;
            }
            if(args[1] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the player to give money to");
                return true;
            }
            if(args[2] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the amount to give");
                return true;
            }
            if(Integer.parseInt(args[2]) <= 0) {
                player.sendMessage(ChatColor.RED + "Amount should be more than 0");
                return true;
            }
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player is offline");
                return true;
            }
            EconomyResponse response = econ.depositPlayer(Bukkit.getServer().getPlayer(args[1]), Integer.parseInt(args[2]));
            player.sendMessage(ChatColor.GREEN + "You have added $" + Integer.parseInt(args[2]) + " to " + args[1]);
        }
        else if(args[0].equalsIgnoreCase("remove")) {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "/money remove (user) (amount)");
                return true;
            }
            if(args[1] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the player to remove money from");
                return true;
            }
            if(args[2] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the amount to remove");
                return true;
            }
            if(Integer.parseInt(args[2]) <= 0) {
                player.sendMessage(ChatColor.RED + "Amount should be more than 0");
                return true;
            }
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player is offline");
                return true;
            }
            EconomyResponse response = econ.withdrawPlayer(Bukkit.getServer().getPlayer(args[1]), Integer.parseInt(args[2]));
            player.sendMessage(ChatColor.GREEN + "You have removed $" + Integer.parseInt(args[2]) + " from " + args[1]);
            }
        else if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 3) {
                player.sendMessage(ChatColor.RED + "/money set (user) (amount)");
                return true;
            }
            if(args[1] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the player you wish to set their balance");
                return true;
            }
            if(args[2] == null) {
                player.sendMessage(ChatColor.RED + "Please specify the amount to set");
                return true;
            }
            if(Integer.parseInt(args[2]) <= 0) {
                player.sendMessage(ChatColor.RED + "Amount should be more than 0");
                return true;
            }
            if(Bukkit.getServer().getPlayer(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "Player is offline");
                return true;
            }
            try {
                boolean response = db.updateOne(Bukkit.getServer().getPlayer(args[1]).getUniqueId().toString().replaceAll("-", "") , Double.parseDouble(args[2]));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            player.sendMessage(ChatColor.GREEN + "You have set " + Bukkit.getServer().getPlayer(args[1]).getName() + "'s balance to " + Integer.parseInt(args[2]));
        }

        return true;
    }
}
