package de.emn4tor.modules.lobby.crates.reward;

import de.emn4tor.modules.lobby.crates.model.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@SuperBuilder
public abstract class BaseReward {
    private final String displayName;
    private ItemStack displayItem;
    private final double weight;
    private final RewardType type;

    public abstract void give(Player player);
}
