package de.emn4tor.utils.holograms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class HologramInstance {
    private final List<ProtocolLine> lines;
    private final Set<UUID> viewers = ConcurrentHashMap.newKeySet();

    public void show(Player p) {
        if (viewers.add(p.getUniqueId())) {
            lines.forEach(line -> line.send(p));
        }
    }

    public void hide(Player p) {
        if (viewers.remove(p.getUniqueId())) {
            lines.forEach(line -> line.destroy(p));
        }
    }

    public Location getCenterLocation() {
        return lines.get(0).getLocation();
    }
}