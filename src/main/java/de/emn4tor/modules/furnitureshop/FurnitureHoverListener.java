package de.emn4tor.modules.furnitureshop;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.economy.rubies.RubyHandler;
import fi.septicuss.tooltips.api.TooltipsAPI;
import fi.septicuss.tooltips.managers.theme.Theme;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FurnitureHoverListener implements Listener {

    private final Map<Player, Integer> tooltipTasks = new HashMap<>();

    @EventHandler
    public void onFurnitureHover(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isInShop(player)) return;

        if (NexoFurniture.findTargetFurniture(player) != null) {
            ItemDisplay itemDisplay = NexoFurniture.findTargetFurniture(player);
            toggleGlow(player, itemDisplay, true);
            String id = NexoFurniture.furnitureMechanic(itemDisplay).getItemID();
            showPrice(player, id);
        }
    }

    public static void toggleGlow(Player player, Entity target, boolean glowEnabled){
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        WrappedDataValue value;
        if (glowEnabled){
            value = new WrappedDataValue(0, WrappedDataWatcher.Registry.get((Type) Byte.class), (byte) 0x40);
        }else value = new WrappedDataValue(0, WrappedDataWatcher.Registry.get((Type) Byte.class), (byte) 0x00);
        packet.getDataValueCollectionModifier().write(0, Collections.singletonList(value));
        packet.getIntegers().write(0, target.getEntityId());
        pm.sendServerPacket(player,packet);
        Bukkit.getScheduler().runTaskLater(YellowMCCoreV2.getInstance(), () -> {
            if (NexoFurniture.findTargetFurniture(player) != target) {
                toggleGlow(player, target, false);
            }
        }, 20L);
    }

    private void showPrice(Player player, String furnitureId) {
        if (tooltipTasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(tooltipTasks.get(player));
        }
        Integer price = FurnitureShopManager.getFurniturePriceMap().get(furnitureId);
        String itemName = PlainTextComponentSerializer.plainText().serialize(NexoItems.itemFromId(furnitureId).getItemName());
        if (price != null) {
            TooltipsAPI.sendTooltip(player, TooltipsAPI.getTheme("default/three-line"), List.of(itemName + "\n<gray>Price: <white>" + price + " {default/ruby}\n<#ffa500>(Click to buy {default/right-click}<#ffa500>)"));
        } else {
            TooltipsAPI.sendTooltip(player, TooltipsAPI.getTheme("default/three-line"), List.of(itemName + "\n<gray>Couldnt find a price \n<red>(Contact an admin)"));
        }
        int taskId = Bukkit.getScheduler().runTaskLater(YellowMCCoreV2.getInstance(), () -> {
            if (NexoFurniture.findTargetFurniture(player) != null) {
                showPrice(player, furnitureId);
            } else {
                player.clearTitle();
                tooltipTasks.remove(player);
            }
        }, 20L).getTaskId();

        tooltipTasks.put(player, taskId);
    }

    @EventHandler
    public void onRightClickFurniture(PlayerInteractEvent event) {
        if (!isInShop(event.getPlayer())) return;
        if(!event.getAction().isRightClick()) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (NexoFurniture.findTargetFurniture(player) != null) {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendRichMessage("<red>Your inventory is full! Please free up some space before buying an item.");
                return;
            }
            ItemDisplay itemDisplay = NexoFurniture.findTargetFurniture(player);
            String id = NexoFurniture.furnitureMechanic(itemDisplay).getItemID();
            Integer price = FurnitureShopManager.getFurniturePriceMap().get(id);
            if (price != null) {
                if (RubyHandler.getRubiesAsync(player.getUniqueId()).join() < price) {
                    player.sendRichMessage("<red>You don't have enough rubies to buy this item!");
                    return;
                }
                player.sendRichMessage("<green>You bought a " + PlainTextComponentSerializer.plainText().serialize(NexoItems.itemFromId(id).getItemName()) + " for <gold>" + price + " <glyph:ruby>");
                player.getInventory().addItem(NexoItems.itemFromId(id).build());
                RubyHandler.removeRubies(player.getUniqueId(), price);
            } else {
                player.sendRichMessage("<red>Could not find the price for this item. Please contact an admin.");
            }
        }
    }

    public static boolean isInShop(Player player) {
        World w = Bukkit.getWorld("world");

        return isBetween(player,
                new Location(w, 65, 73, -115),
                new Location(w, 44, 64, -123)
        ) || isBetween(player,
                new Location(w, 47, 72, -132),
                new Location(w, 58, 64, -111)
        );
    }

    public static boolean isBetween(Player player, Location loc1, Location loc2) {
        Location pLoc = player.getLocation();

        if (!pLoc.getWorld().equals(loc1.getWorld()) || !pLoc.getWorld().equals(loc2.getWorld()))
            return false;

        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());

        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());

        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

        return pLoc.getX() >= minX && pLoc.getX() <= maxX
            && pLoc.getY() >= minY && pLoc.getY() <= maxY
            && pLoc.getZ() >= minZ && pLoc.getZ() <= maxZ;
    }

}