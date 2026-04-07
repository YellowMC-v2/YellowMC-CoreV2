package de.emn4tor.modules.lobby.crates.reward;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import com.nexomc.nexo.api.NexoItems;

public class RewardItem {

    private final ItemStack itemStack;
    private static final Material FALLBACK = Material.PAPER;

    /**
     * Constructor parses the string immediately to ensure the RewardItem
     * is always "ready to use".
     */
    public RewardItem(String itemString) {
        this.itemStack = parse(itemString);
    }

    /**
     * @return The safe ItemStack representation of this reward.
     */
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    private ItemStack parse(String input) {
        if (input == null || input.isBlank()) {
            return new ItemStack(FALLBACK);
        }

        String cleaned = input.trim();

        if (cleaned.toLowerCase().startsWith("nexo:")) {
            try {
                YellowMCCoreV2.getInstance().getLogger().warning("Parsing Nexo item: " + cleaned);
                String id = cleaned.substring(5);
                var nexoBuilder = NexoItems.itemFromId(id);
                if (nexoBuilder != null) {
                    return nexoBuilder.build();
                } else {
                    YellowMCCoreV2.getInstance().getLogger().warning("Nexo item not found: " + id);
                }
            } catch (Exception e) {
                YellowMCCoreV2.getInstance().getLogger().warning("Error parsing Nexo item: " + cleaned + " - " + e.getMessage());
            }
            return new ItemStack(FALLBACK);
        }

        try {
            Material mat = Material.matchMaterial(cleaned.toUpperCase());
            if (mat != null && mat.isItem()) {
                return new ItemStack(mat);
            }
        } catch (Exception ignored) {
        }

        return new ItemStack(FALLBACK);
    }
}