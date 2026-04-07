package de.emn4tor.modules.global.economy.coins.api;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.entity.Player;

public class EconomyHandler {
    private static Economy economy;

    static {
        setupEconomy();
    }

    public static void setupEconomy() {
        if (economy != null) return;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            Bukkit.getLogger().info("[EconomyHandler] Vault Economy successfully hooked!");
        } else {
            Bukkit.getLogger().severe("[EconomyHandler] Vault Economy NOT FOUND! Transactions will fail.");
        }
    }

    public static boolean hasEconomy() {
        return economy != null;
    }

    public static double getCoins(OfflinePlayer player) {
        return hasEconomy() ? economy.getBalance(player) : 0;
    }

    public static boolean hasCoins(OfflinePlayer player, double amount) {
        return hasEconomy() && economy.has(player, amount);
    }

    public static boolean addCoins(OfflinePlayer player, double amount) {
        return hasEconomy() && economy.depositPlayer(player, amount).transactionSuccess();
    }

    public static boolean removeCoins(OfflinePlayer player, double amount) {
        return hasEconomy() && economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public static boolean setCoins(OfflinePlayer player, double amount) {
        if (!hasEconomy()) return false;
        double currentBalance = economy.getBalance(player);
        double difference = amount - currentBalance;
        return difference >= 0 ? addCoins(player, difference) : removeCoins(player, -difference);
    }

    public static boolean createAccount(OfflinePlayer player) {
        return hasEconomy() && economy.createPlayerAccount(player);
    }

    public static boolean accountExists(OfflinePlayer player) {
        return hasEconomy() && economy.hasAccount(player);
    }


    public static boolean purchaseItem(Player player, double cost) {
        return hasEconomy() && hasCoins(player, cost) && removeCoins(player, cost);
    }
}
