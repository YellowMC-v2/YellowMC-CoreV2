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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FurnitureHoverListener implements Listener {

    Map<String, Integer> furniturePriceMap = new HashMap<>(
            Map.of(
                    "hcs_chair", 50,
                    "hcs_counter_inner_end", 100,
                    "smelter", 150
            )
    );

    @EventHandler
    public void onFurnitureHover(PlayerMoveEvent event) {
        Player player = event.getPlayer();

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
        Integer price = furniturePriceMap.get(furnitureId);
        String itemName = PlainTextComponentSerializer.plainText().serialize(NexoItems.itemFromId(furnitureId).getItemName());
        if (price != null) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize( itemName + " <gray>Price: <white>" + price + " <glyph:ruby> <#ffa500>(Click to buy)"));
        } else {
            player.sendActionBar(MiniMessage.miniMessage().deserialize(itemName + " <gray>Couldnt find a price <glyph:ruby> <red>(Contact an admin)"));
        }
    }



}