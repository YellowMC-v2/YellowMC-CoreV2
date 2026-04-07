package de.emn4tor.modules.lobby.crates.ui;

import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CratePreviewGUI {

    private final Crate crate;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CratePreviewGUI(Crate crate) {
        this.crate = crate;
    }

    public void open(Player player) {
        int size = Math.min(54, ((crate.getRewards().size() / 9) + 1) * 9);

        Component title = mm.deserialize("<gray>Preview: <white>" + crate.getDisplayName());
        Inventory inv = Bukkit.createInventory(null, size, title);

        List<BaseReward> sortedRewards = new ArrayList<>(crate.getRewards());
        sortedRewards.sort(Comparator.comparingDouble(BaseReward::getWeight));

        double totalWeight = sortedRewards.stream().mapToDouble(BaseReward::getWeight).sum();

        for (int i = 0; i < sortedRewards.size() && i < 54; i++) {
            BaseReward reward = sortedRewards.get(i);
            inv.setItem(i, createPreviewItem(reward, totalWeight));
        }

        player.openInventory(inv);
    }

    private ItemStack createPreviewItem(BaseReward reward, double totalWeight) {
        ItemStack item = reward.getDisplayItem().clone();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
            if (lore == null) lore = new ArrayList<>();

            double chance = (reward.getWeight() / totalWeight) * 100;
            String chanceFormatted = String.format("%.2f", chance);

            lore.add(Component.empty());
            lore.add(mm.deserialize("<gray>Chance: <yellow>" + chanceFormatted + "%")
                    .decoration(TextDecoration.ITALIC, false));

            meta.displayName(mm.deserialize(reward.getDisplayName())
                    .decoration(TextDecoration.ITALIC, false));

            meta.lore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}