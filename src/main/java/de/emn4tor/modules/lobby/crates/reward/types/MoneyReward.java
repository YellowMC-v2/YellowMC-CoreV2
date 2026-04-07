package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.modules.global.economy.coins.api.EconomyHandler;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;

@Getter
@SuperBuilder
public class MoneyReward extends BaseReward {
    private final double amount;

    @Override
    public void give(Player player) {
        player.sendMessage(EconomyHandler.addCoins(player, amount)
                ? "<green>You received <yellow>" + amount + " Coins!"
                : "<red>Failed to add coins to your account. Please contact support.");
    }
}
