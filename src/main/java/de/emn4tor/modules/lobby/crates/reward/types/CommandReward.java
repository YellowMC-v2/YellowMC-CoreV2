package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@SuperBuilder
public class CommandReward extends BaseReward {
    private final String command;

    @Override
    public void give(Player player) {
        String finalCommand = command.replace("%player%", player.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }
}
