package de.emn4tor.modules.global.starteritems;

/*
 * @author: Emn4tor
 * @created: 19.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@ModuleInfo(name = "StarterItemsModule")
public class StarterItemsModule implements Module, Listener {

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
                    var inv = player.getInventory();
                    inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                    inv.setItem(0, new ItemStack(Material.STONE_SWORD));
                    inv.setItem(1, new ItemStack(Material.STONE_PICKAXE));
                    inv.setItem(2, new ItemStack(Material.STONE_AXE));
                    inv.setItem(3, new ItemStack(Material.STONE_SHOVEL));
                    inv.setItem(4, new ItemStack(Material.BREAD, 16));

                    YellowMCCoreV2.getCoinService().addCoins(player.getUniqueId(), 1000);

                    var lpApi = LuckPermsProvider.get();
                    var user = lpApi.getUserManager().getUser(player.getUniqueId());

                    if (user != null) {
                        user.data().add(Node.builder("core.firstjoin").build());
                        lpApi.getUserManager().saveUser(user);
                    }
                }
            }.runTaskLater(YellowMCCoreV2.getInstance(), 2 * 20L);
        }
    }
}