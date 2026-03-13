package de.emn4tor.modules.lobby.crates.reward.types;

import com.nexomc.nexo.api.NexoItems;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Getter
@SuperBuilder
public class NexoItemReward extends BaseReward {
    private final String itemId;
    private final int amount;

    @Override
    public void give(Player player) {
        ItemStack rewardItem = Objects.requireNonNullElse(NexoItems.itemFromId(itemId).build(), NexoItems.itemFromId("workstation").build());
        rewardItem.setAmount(amount);
        player.getInventory().addItem(rewardItem);
    }
}
