package de.emn4tor.modules.smp.claimsystem.listener;

import de.emn4tor.modules.smp.claimsystem.logic.ClaimManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Event listener that prevents players from performing prohibited actions
 * within claimed chunks.
 */
public class ProhibitedActionsListener implements Listener {

    private final ClaimManager claimManager;

    /**
     * Constructs a new ProhibitedActionsListener.
     *
     * @param claimManager the ClaimManager instance used for claim validation
     */
    public ProhibitedActionsListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkAndCancel(event.getPlayer(), event.getBlock().getChunk(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkAndCancel(event.getPlayer(), event.getBlock().getChunk(), event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            checkAndCancel(event.getPlayer(), event.getClickedBlock().getChunk(), event);
        }
    }

    @EventHandler
    public void onPlayerHitEntity(org.bukkit.event.entity.EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            checkAndCancel(player, event.getEntity().getLocation().getChunk(), event);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerInteractEntityEvent event) {
        checkAndCancel(event.getPlayer(), event.getRightClicked().getLocation().getChunk(), event);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (org.bukkit.block.Block block : event.blockList()) {
            if (claimManager.isClaimed(block.getWorld().getName(), block.getChunk().getX(), block.getChunk().getZ())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Creeper) {
            for (org.bukkit.block.Block block : event.blockList()) {
                if (claimManager.isClaimed(block.getWorld().getName(), block.getChunk().getX(), block.getChunk().getZ())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    /**
     * Checks if the player can modify the chunk, and cancels the event if not.
     *
     * @param player the player attempting the action
     * @param chunk the chunk where the action is happening
     * @param event the event to cancel if necessary
     */
    private void checkAndCancel(Player player, Chunk chunk, org.bukkit.event.Cancellable event) {
        boolean allowed = claimManager.canModify(
                player.getUniqueId(),
                chunk.getWorld().getName(),
                chunk.getX(),
                chunk.getZ()
        );

        if (!allowed) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot modify blocks in this claimed area."); // or sendRichMessage if available
        }
    }
}
