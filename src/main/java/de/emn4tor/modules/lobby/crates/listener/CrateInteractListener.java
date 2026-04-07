package de.emn4tor.modules.lobby.crates.listener;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.registry.CrateManager;
import de.emn4tor.modules.lobby.crates.registry.CrateRegistry;
import de.emn4tor.modules.lobby.crates.ui.CratePreviewGUI;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

@AllArgsConstructor
public class CrateInteractListener implements Listener {
    private final CrateRegistry registry;
    private final CrateManager crateManager;

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;
        if (!NexoFurniture.isFurniture(event.getClickedBlock().getLocation())) return;

        registry.getCrateAt(event.getClickedBlock().getLocation()).ifPresent(crate -> {
            event.setCancelled(true);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                crateManager.openCrate(player, crate);
            } else
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                new CratePreviewGUI(crate).open(player);
            }
        });

    }

}
