package de.emn4tor.utils.holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class ProtocolLine {
    private final int id = ThreadLocalRandom.current().nextInt(500000, 1000000);
    private final UUID uuid = UUID.randomUUID();

    @Getter private final Location location;
    private final String text;

    public void send(Player p) {
        var pm = ProtocolLibrary.getProtocolManager();

        // 1. Spawn Packet
        PacketContainer spawn = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawn.getIntegers().write(0, id);
        spawn.getUUIDs().write(0, uuid);
        spawn.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        spawn.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());

        // 2. Metadata Packet
        PacketContainer meta = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0, id);

        // Use modern registry accessors
        var byteRegistry = WrappedDataWatcher.Registry.get(Byte.class);
        var boolRegistry = WrappedDataWatcher.Registry.get(Boolean.class);
        var chatRegistry = WrappedDataWatcher.Registry.getChatComponentSerializer(true);

        // Build the DataValue list directly to avoid the ClassCastException
        List<WrappedDataValue> dataValues = new ArrayList<>();

        // Index 0: Status (0x20 = Invisible)
        dataValues.add(new WrappedDataValue(0, byteRegistry, (byte) 0x20));

        // Index 2: Custom Name
        String json = GsonComponentSerializer.gson().serialize(MiniMessage.miniMessage().deserialize(text));
        Object chatComponent = WrappedChatComponent.fromJson(json).getHandle();
        dataValues.add(new WrappedDataValue(2, chatRegistry, Optional.of(chatComponent)));

        // Index 3: Custom Name Visible
        dataValues.add(new WrappedDataValue(3, boolRegistry, true));

        // Index 15: Armor Stand Mask (0x10 = Marker)
        dataValues.add(new WrappedDataValue(15, byteRegistry, (byte) 0x10));

        // Write to the correct modifier for 1.19.3+
        meta.getDataValueCollectionModifier().write(0, dataValues);

        pm.sendServerPacket(p, spawn);
        pm.sendServerPacket(p, meta);
    }

    public void destroy(Player p) {
        PacketContainer destroy = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        // Modern Entity Destroy uses a list of Integers
        destroy.getIntLists().write(0, List.of(id));
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, destroy);
    }
}