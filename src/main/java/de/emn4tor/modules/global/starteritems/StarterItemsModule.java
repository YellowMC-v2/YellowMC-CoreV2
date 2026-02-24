package de.emn4tor.modules.global.starteritems;

/*
 *  @author: Emn4tor
 *  @created: 19.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.economy.coins.api.EconomyHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


@ModuleInfo(name="StarterItemsModule")
public class StarterItemsModule implements Module, Listener {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
    }

    @EventHandler
    public void onFirstPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("core.firstjoin")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                    player.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));
                    player.getInventory().setItem(1, new ItemStack(Material.STONE_PICKAXE));
                    player.getInventory().setItem(2, new ItemStack(Material.STONE_AXE));
                    player.getInventory().setItem(3, new ItemStack(Material.STONE_SHOVEL));
                    player.getInventory().setItem(4, new ItemStack(Material.BREAD, 16));
                    EconomyHandler.addCoins(player, 1000);
                    LuckPerms api = LuckPermsProvider.get();

                    User user = api.getUserManager().getUser(player.getUniqueId());
                    user.data().add(Node.builder("core.firstjoin").build());
                    api.getUserManager().saveUser(user);
                }
            }.runTaskLater(YellowMCCoreV2.getInstance(), 2 * 20);
        }
    }

}