package de.emn4tor.modules.lobby.crates.registry;

import de.emn4tor.modules.lobby.crates.keys.CrateKeyRepository;
import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import de.emn4tor.modules.lobby.crates.ui.CrateAnimation;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CrateManager {
    private final Map<Player, CrateSession> activeSessions = new HashMap<>();
    private final CrateKeyRepository keyRepository;

    public CrateManager(CrateKeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    private record CrateSession(CrateAnimation animation, BaseReward reward, Crate crate) {}

    /**
     * Checks if a player has at least one key for the given crate.
     */
    public boolean hasKey(Player player, Crate crate) {
        return keyRepository.getKeys(player.getUniqueId(), crate.getName()) > 0;
    }

    public void handleGUIClose(Player player) {
        CrateSession session = activeSessions.remove(player);

        if (session != null) {
            session.animation().cancel();

            BaseReward reward = session.reward();
            Crate crate = session.crate();

            reward.give(player);

            player.sendRichMessage("<yellow>Crate animation skipped. <green>You opened a "
                    + crate.getDisplayName() + " <green>and received: <yellow>"
                    + reward.getDisplayName());
        }
    }

    /**
     * Handles the full logic of opening a crate.
     */
    public void openCrate(Player player, Crate crate) {
        if (activeSessions.containsKey(player)) {
            player.sendMessage("§cYou are already opening a crate!");
            return;
        }


        boolean success = keyRepository.consumeKey(player.getUniqueId(), crate.getName());

        if (!success) {
            player.sendMessage("§cYou don't have a " + crate.getDisplayName() + " §ckey!");
            return;
        }

        Optional<BaseReward> rewardOptional = rollReward(crate);
        if (rewardOptional.isEmpty()) {
            player.sendMessage("§cThis crate appears to be empty! Contact an administrator.");
            keyRepository.giveKeys(player.getUniqueId(), crate.getName(), 1);
            return;
        }

        BaseReward winningReward = rewardOptional.get();
        CrateAnimation animation = new CrateAnimation();

        activeSessions.put(player, new CrateSession(animation, winningReward, crate));

        animation.spin(player, crate, winningReward, () -> {
            if (activeSessions.containsKey(player)) {
                winningReward.give(player);
                player.sendRichMessage("<green>You opened a " + crate.getDisplayName()
                        + " <green>and received: <yellow>" + winningReward.getDisplayName());
                activeSessions.remove(player);
            }
        });
    }

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