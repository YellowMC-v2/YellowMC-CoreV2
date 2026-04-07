package de.emn4tor.modules.lobby.crates.ui;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Random;

public class CrateAnimation {

    // Border slots (0-9, 17-26) that will be filled with random glass panes
    List<Integer> borderSlots = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);
    // Free slots where the "spinning" items will appear
    private static final List<Integer> FREE_SLOTS = List.of(10, 11, 12, 13, 14, 15, 16);

    // The "winner" display slot in the center of the inventory
    private static final int WINNER_SLOT = 13;

    private static final Random RANDOM = new Random();

    private static final Material[] GLASS_PANES = {
            Material.WHITE_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE
    };

    // Random filler materials for the spinning free slots TODO: replace with displayItem from BaseItem (Not implemented yet)
    private static final Material[] FILLER_MATERIALS = {
            Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_INGOT,
            Material.EMERALD, Material.COAL, Material.REDSTONE,
            Material.LAPIS_LAZULI, Material.QUARTZ, Material.AMETHYST_SHARD,
            Material.NETHER_STAR, Material.ENDER_PEARL, Material.BLAZE_ROD,
            Material.EXPERIENCE_BOTTLE, Material.TOTEM_OF_UNDYING, Material.TRIDENT
    };

    private static final int TOTAL_STEPS = 40;
    private static final double SLOWDOWN_THRESHOLD = 0.6;

    public void spin(Player player, Crate crate, BaseReward reward, Runnable onFinish) {
        Inventory inv = YellowMCCoreV2.getInstance().getServer().createInventory(
                new CrateInvHolder(),
                27,
                MiniMessage.miniMessage().deserialize("<red>Spinning...</red>")
        );

        randomizeBorder(inv);
        player.openInventory(inv);

        runAnimationStep(player, inv, crate, reward, 0, onFinish);
    }

    private void runAnimationStep(Player player, Inventory inv, Crate crate, BaseReward reward, int currentStep, Runnable onFinish) {
        if (!player.isOnline()) return;

        if (currentStep >= TOTAL_STEPS) {
            placeWinner(inv, reward);
            player.updateInventory();
            player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1.0f, 2.0f);
            if (onFinish != null) onFinish.run();
            return;
        }

        randomizeFreeSlots(inv, crate);

        boolean isNearEnd = currentStep >= TOTAL_STEPS - 3;
        if (isNearEnd) {
            inv.setItem(WINNER_SLOT, buildDisplayItem(reward));
        }

        player.updateInventory();
        player.playSound(player.getLocation(), "minecraft:block.note_block.hat", 1.0f, 1.0f);

        long nextDelay = computeDelay(currentStep);

        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                runAnimationStep(player, inv, crate, reward, currentStep + 1, onFinish);
            }
        }.runTaskLater(YellowMCCoreV2.getInstance(), nextDelay);
    }

    /**
     * Computes an increasing tick delay based on how far into the animation we are.
     * Early steps = fast (2 ticks), late steps = slow (up to 10 ticks).
     */
    private long computeDelay(int currentStep) {
        double progress = (double) currentStep / TOTAL_STEPS;
        if (progress < SLOWDOWN_THRESHOLD) return 2L;
        double slowProgress = (progress - SLOWDOWN_THRESHOLD) / (1.0 - SLOWDOWN_THRESHOLD);
        return Math.round(2 + slowProgress * 8);
    }


    private void randomizeFreeSlots(Inventory inventory, Crate crate) {
        List<BaseReward> rewards = crate.getRewards();
        for (int slot : FREE_SLOTS) {
            BaseReward randomReward = rewards.get(RANDOM.nextInt(rewards.size()));
            inventory.setItem(slot, buildDisplayItem(randomReward));
        }
    }

    private void placeWinner(Inventory inventory, BaseReward reward) {
        for (int slot : FREE_SLOTS) {
            inventory.setItem(slot, null);
        }
        inventory.setItem(WINNER_SLOT, buildDisplayItem(reward));
    }

    /**
     * Builds a display ItemStack for the reward.
     * Falls back to PAPER if the reward has no custom display item.
     */
    private ItemStack buildDisplayItem(BaseReward reward) {
        ItemStack item = reward.getDisplayItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize("<gold><bold>" + reward.getDisplayName()));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void randomizeBorder(Inventory inventory) {
        for (int slot : borderSlots) {
            inventory.setItem(slot, new ItemStack(GLASS_PANES[RANDOM.nextInt(GLASS_PANES.length)]));
        }
    }
}