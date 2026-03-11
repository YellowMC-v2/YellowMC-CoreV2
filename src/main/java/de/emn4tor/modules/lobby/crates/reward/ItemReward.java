package de.emn4tor.modules.lobby.crates.reward;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@SuperBuilder
public class ItemReward extends BaseReward {
    private final ItemStack itemStack;
    private final int amount;

    @Override
    public void give(Player player) {
        ItemStack rewardItem = itemStack.clone();
        rewardItem.setAmount(amount);
        player.getInventory().addItem(rewardItem);
    }
}

