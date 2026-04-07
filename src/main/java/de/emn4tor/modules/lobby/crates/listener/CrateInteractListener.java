package de.emn4tor.modules.lobby.crates.listener;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.registry.CrateManager;
import de.emn4tor.modules.lobby.crates.registry.CrateRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@AllArgsConstructor
public class CrateInteractListener implements Listener {
    private final CrateRegistry registry;
    private final CrateManager crateManager;

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!NexoFurniture.isFurniture(event.getClickedBlock().getLocation())) return;

        registry.getCrateAt(event.getClickedBlock().getLocation()).ifPresent(crate -> {
            event.setCancelled(true);

            handleCrateClick(event.getPlayer(), crate);
        });

    }

    private void handleCrateClick(Player player, Crate crate) {
        crateManager.openCrate(player, crate);
    }
}
