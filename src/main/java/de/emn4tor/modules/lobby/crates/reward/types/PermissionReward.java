package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

@Getter
@SuperBuilder
public class PermissionReward extends BaseReward {
    private final String permission;

    @Override
    public void give(Player player) {
        YellowMCCoreV2.getInstance().getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), user -> {
            user.data().add(Node.builder(permission).build());
        });
    }
}
