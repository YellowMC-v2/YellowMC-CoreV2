package de.emn4tor.modules.lobby.crates.reward.types;


import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.CratesModule;
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
        CratesModule module = CratesModule.getInstance();

        if (module != null && module.getKeyRepository() != null) {
            module.getKeyRepository().giveKeys(player.getUniqueId(), crateId, amount);

            player.sendRichMessage("<gray>You received <yellow>" + amount + "x " + crateId + " <gray>Keys!");
        }
    }
}