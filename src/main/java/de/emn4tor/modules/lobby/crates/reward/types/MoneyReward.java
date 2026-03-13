package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.modules.global.economy.coins.api.EconomyHandler;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;

@Getter
@SuperBuilder
public class MoneyReward extends BaseReward {
    private final int amount;

    @Override
    public void give(Player player) {
        EconomyHandler.addCoins(player, amount);
    }
}
