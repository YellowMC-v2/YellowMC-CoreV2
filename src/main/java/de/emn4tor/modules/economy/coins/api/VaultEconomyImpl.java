package de.emn4tor.modules.economy.coins.api;

/*
 *  @author: Emn4tor
 *  @created: 20.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.EconomyModule;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class VaultEconomyImpl extends AbstractEconomy {
    EconomyManager economyManager = EconomyModule.getEconomyManager();

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Münzen";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        long coins = (long) v;
        String name = (coins == 1) ? "Münze" : "Münzen";
        return String.format("%,d %s", coins, name);
    }

    @Override
    public String currencyNamePlural() {
        return "Münzen";
    }

    @Override
    public String currencyNameSingular() {
        return "Münze";
    }

    @Override
    public boolean hasAccount(String s) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.hasAccount(uuid).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to check account for " + uuid, e);
            return false;
        }
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.hasAccount(uuid).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to check account for " + uuid, e);
            return false;
        }
    }

    @Override
    public double getBalance(String s) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.getCoins(uuid).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to get balance for " + uuid, e);
            return 0;
        }
    }

    @Override
    public double getBalance(String s, String s1) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.getCoins(uuid).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to get balance for " + uuid, e);
            return 0;
        }
    }

    @Override
    public boolean has(String s, double v) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.hasEnoughCoins(uuid, (int) v).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to check balance for " + uuid, e);
            return false;
        }
    }

    @Override
    public boolean has(String s, String s1, double v) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        try {
            return economyManager.hasEnoughCoins(uuid, (int) v).get();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to check balance for " + uuid, e);
            return false;
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return getEconomyResponse(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return getEconomyResponse(s, v);
    }

    @NotNull
    private EconomyResponse getEconomyResponse(String s, double v) {
        Player player = Bukkit.getOfflinePlayer(s).getPlayer();
        try {
            economyManager.removeCoins(player, (int) v).get();
            return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to withdraw balance for " + player, e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return getEconomyResponseDeposit(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return getEconomyResponseDeposit(s, v);
    }

    @NotNull
    private EconomyResponse getEconomyResponseDeposit(String s, double v) {
        Player player = Bukkit.getOfflinePlayer(s).getPlayer();
        try {
            economyManager.addCoins(player, (int) v);
            return new EconomyResponse(v, getBalance(s), EconomyResponse.ResponseType.SUCCESS, null);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault: failed to deposit balance for " + player, e);
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);
        }
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        economyManager.createAccount(uuid, 100);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        UUID uuid = Bukkit.getOfflinePlayer(s).getUniqueId();
        economyManager.createAccount(uuid, 100);
        return true;
    }
}
