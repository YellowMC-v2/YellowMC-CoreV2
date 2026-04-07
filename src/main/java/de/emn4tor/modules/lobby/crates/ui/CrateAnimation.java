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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class CrateAnimation {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Random RANDOM = new Random();

    private static final int INVENTORY_SIZE = 27;
    private static final int WINNER_SLOT = 13;
    private static final int TOTAL_STEPS = 40;
    private static final double SLOWDOWN_THRESHOLD = 0.6;

    private static final int[] BORDER_SLOTS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8,
            18, 19, 20, 21, 22, 23, 24, 25, 26
    };

    private static final int[] FREE_SLOTS = { 9, 10, 11, 12, 13, 14, 15, 16, 17 };

    private static final Material[] GLASS_PANES = {
            Material.WHITE_STAINED_GLASS_PANE,      Material.ORANGE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,    Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,     Material.LIME_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE,       Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,     Material.BLUE_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,      Material.GREEN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,        Material.BLACK_STAINED_GLASS_PANE
    };

    private static final ItemStack[] GLASS_PANE_ITEMS;

    static {
        GLASS_PANE_ITEMS = new ItemStack[GLASS_PANES.length];
        for (int i = 0; i < GLASS_PANES.length; i++) {
            GLASS_PANE_ITEMS[i] = new ItemStack(GLASS_PANES[i]);
        }
    }

    private BukkitTask currentTask;

    public void cancel() {
        if (currentTask != null) {
            currentTask.cancel();
        }
    }

    public void spin(Player player, Crate crate, BaseReward reward, Runnable onFinish) {
        Inventory inv = YellowMCCoreV2.getInstance().getServer().createInventory(
                new CrateInvHolder(),
                INVENTORY_SIZE,
                MM.deserialize("<red><bold>Opening Crate…</bold></red>")
        );

        fillBorder(inv);
        player.openInventory(inv);

        scheduleNextStep(player, inv, crate, reward, 0, onFinish);
    }

    private void scheduleNextStep(
            Player player, Inventory inv,
            Crate crate, BaseReward reward,
            int step, Runnable onFinish
    ) {
        this.currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !(player.getOpenInventory().getTopInventory().getHolder() instanceof CrateInvHolder)) {
                    cancel();
                    return;
                }

                if (step >= TOTAL_STEPS) {
                    finalizeAnimation(player, inv, reward, onFinish);
                    return;
                }

                fillBorder(inv);
                fillFreeSlots(inv, crate, reward, step);
                player.updateInventory();
                player.playSound(player.getLocation(), "minecraft:block.note_block.hat", 1.0f, pitchForStep(step));

                scheduleNextStep(player, inv, crate, reward, step + 1, onFinish);
            }
        }.runTaskLater(YellowMCCoreV2.getInstance(), computeDelay(step));
    }

    private void finalizeAnimation(Player player, Inventory inv, BaseReward reward, Runnable onFinish) {
        for (int slot : FREE_SLOTS) {
            inv.setItem(slot, null);
        }
        inv.setItem(WINNER_SLOT, buildDisplayItem(reward));
        player.updateInventory();
        player.playSound(player.getLocation(), "minecraft:block.note_block.pling", 1.0f, 2.0f);
        if (onFinish != null) onFinish.run();
    }

    private void fillFreeSlots(Inventory inv, Crate crate, BaseReward reward, int step) {
        List<BaseReward> rewards = crate.getRewards();
        boolean showWinner = step >= TOTAL_STEPS - 3;

        for (int slot : FREE_SLOTS) {
            if (showWinner && slot == WINNER_SLOT) {
                inv.setItem(slot, buildDisplayItem(reward));
            } else {
                BaseReward pick = rewards.get(RANDOM.nextInt(rewards.size()));
                inv.setItem(slot, buildDisplayItem(pick));
            }
        }
    }

    private void fillBorder(Inventory inv) {
        for (int slot : BORDER_SLOTS) {
            inv.setItem(slot, GLASS_PANE_ITEMS[RANDOM.nextInt(GLASS_PANE_ITEMS.length)]);
        }
    }

    private static long computeDelay(int step) {
        double progress = (double) step / TOTAL_STEPS;
        if (progress < SLOWDOWN_THRESHOLD) return 2L;
        double t = (progress - SLOWDOWN_THRESHOLD) / (1.0 - SLOWDOWN_THRESHOLD);
        return Math.round(2 + t * 8);
    }

    private static float pitchForStep(int step) {
        double progress = (double) step / TOTAL_STEPS;
        return (float) (0.8 + progress * 0.4);
    }

    private static ItemStack buildDisplayItem(BaseReward reward) {
        ItemStack item = reward.getDisplayItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MM.deserialize("<gold><bold>" + reward.getDisplayName() + "</bold></gold>"));
            item.setItemMeta(meta);
        }
        return item;
    }
}