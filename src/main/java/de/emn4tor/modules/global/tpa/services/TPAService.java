package de.emn4tor.modules.global.tpa.services;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.tpa.api.TeleportAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TPAService {
    private final YellowMCCoreV2 core;
    private final TeleportAPI teleportAPI;
    private final Map<UUID, UUID> tpaRequest = new ConcurrentHashMap<>();

    public TPAService(YellowMCCoreV2 core, TeleportAPI teleportAPI) {
        this.core = core;
        this.teleportAPI = teleportAPI;
    }

    public void sendRequest(@NonNull Player sender, @NonNull Player target) {
        this.tpaRequest.put(target.getUniqueId(), sender.getUniqueId());

        sender.sendMessage("TPA Request an " + target.getName() + " verschickt.");

        Bukkit.getScheduler().runTaskLater(this.core, () -> {
            if (this.tpaRequest.get(target.getUniqueId()) != null &&
                    this.tpaRequest.get(target.getUniqueId()).equals(sender.getUniqueId())) {
                this.tpaRequest.remove(target.getUniqueId());
                sender.sendMessage("TPA-Anfrage abgelaufen!");
            }
        }, 20 * 60 * 5);
    }

    public void acceptRequest(@NonNull Player target, @NonNull Player sender) {
        var requesterUUID = this.tpaRequest.get(target.getUniqueId());

        if (requesterUUID != null && requesterUUID.equals(sender.getUniqueId())) {
            this.teleportAPI.teleport(sender, target);

            this.tpaRequest.remove(target.getUniqueId());
        } else {
            target.sendMessage("Keine aktive TPA gefunden.");
        }
    }
}
