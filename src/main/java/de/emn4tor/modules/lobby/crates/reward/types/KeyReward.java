package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.entity.Player;

@Getter
@SuperBuilder
public class KeyReward extends BaseReward {
    private final String crateId;
    private final int amount;

    @Override
    public void give(Player player) {
        player.sendRichMessage("Not implemented yet, womp womp - no reward for you :P");
        //TODO: Implement KeyReward logic
    }
}
