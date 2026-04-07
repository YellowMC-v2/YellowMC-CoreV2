package de.emn4tor.modules.lobby.crates.registry;

import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import de.emn4tor.modules.lobby.crates.ui.CrateAnimation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class CrateManager {
    List<Player> openingPlayers = new ArrayList<>();

    private boolean hasKey(Player player, Crate crate) {
        return true;
    }



    /**
     * Handles the full logic of opening a crate.
     */
    public void openCrate(Player player, Crate crate) {
        if (openingPlayers.contains(player)) {
            player.sendMessage("§cYou are already opening a crate!");
            return;
        }
        if (!hasKey(player, crate)) {
            player.sendMessage("§cYou don't have the required key to open this crate!");
            return;
        }

        Optional<BaseReward> rewardOptional = rollReward(crate);
        if (rewardOptional.isEmpty()) {
            player.sendMessage("§cThis crate appears to be empty! Contact an administrator.");
            return;
        }

        BaseReward winningReward = rewardOptional.get();
        openingPlayers.add(player);

        new CrateAnimation().spin(player, crate, winningReward, () -> {
            winningReward.give(player);
            openingPlayers.remove(player);
            player.sendRichMessage("<green>You opened a " + crate.getDisplayName()
                    + " <green>and received: <yellow>" + winningReward.getDisplayName());
            openingPlayers.remove(player);
        });
    }

    /**
     * Weighted random selection logic.
     * Keeps the math out of the Crate model.
     */
    private Optional<BaseReward> rollReward(Crate crate) {
        if (crate.getRewards().isEmpty()) return Optional.empty();

        double totalWeight = crate.getRewards().stream()
                .mapToDouble(BaseReward::getWeight)
                .sum();

        double randomValue = ThreadLocalRandom.current().nextDouble() * totalWeight;
        double currentSum = 0;

        for (BaseReward reward : crate.getRewards()) {
            currentSum += reward.getWeight();
            if (randomValue <= currentSum) {
                return Optional.of(reward);
            }
        }
        return Optional.of(crate.getRewards().get(0));
    }
}