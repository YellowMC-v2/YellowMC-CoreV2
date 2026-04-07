package de.emn4tor.modules.lobby.crates.reward.types;

import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
@SuperBuilder
public class ItemReward extends BaseReward {
    private String material;
    private List<String> lore;
    private List<String> enchantments;
    private List<String> itemFlags;
    private final int amount;

    @Override
    public void give(Player player) {
        Material mat = Material.matchMaterial(material.toUpperCase());
        if (mat == null) { 
            mat = Material.PAPER;
        }

        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        var mm = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();

        // Set name
        if (this.getDisplayName() != null) {
            meta.displayName(mm.deserialize(this.getDisplayName()));
        }

        // Set Lore
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore.stream()
                    .map(mm::deserialize)
                    .toList());
        }

        // Add Enchantments
        if (enchantments != null) {
            for (String entry : enchantments) {
                String[] split = entry.split(":");
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(split[0].toLowerCase()));
                if (ench != null) {
                    int level = split.length > 1 ? Integer.parseInt(split[1]) : 1;
                    meta.addEnchant(ench, level, true);
                }
            }
        }

        // Add Item Flags
        if (itemFlags != null) {
            for (String flagName : itemFlags) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        item.setItemMeta(meta);
        player.getInventory().addItem(item);
    }
}

