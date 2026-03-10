package de.emn4tor.modules.global.tpa.commands;

import de.emn4tor.modules.global.tpa.api.TeleportAPI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public final class RTPCommand implements BasicCommand {
    private final TeleportAPI teleportAPI;

    public RTPCommand(TeleportAPI teleportAPI) {
        this.teleportAPI = teleportAPI;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) {
            return;
        }

        this.teleportAPI.teleport(player, player.getLocation().add(10,0,10));
    }
}
