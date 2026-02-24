package de.emn4tor.modules.global.commands.languagesel;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InvCloseListener implements Listener {


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (LanguageInventoryListener.switchingPage.remove(player.getUniqueId())) return;

        if (event.getInventory().getHolder() instanceof LanguageInvHolder) {
            player.sendRichMessage("" + YellowMCCoreV2.getLocaleService().getLocaleNoFallBack(player.getUniqueId()));
            if (YellowMCCoreV2.getLocaleService().getLocaleNoFallBack(player.getUniqueId()) == null) {
                Bukkit.getScheduler().runTaskLater(
                        YellowMCCoreV2.getInstance(),
                        () -> LanguageCommand.openLangGUI(player, 0),
                        1L
                );
            }
        }
    }
}
