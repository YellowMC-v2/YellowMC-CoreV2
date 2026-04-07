package de.emn4tor.modules.lobby.crates.reward;

import de.emn4tor.modules.lobby.crates.model.RewardType;
import de.emn4tor.modules.lobby.crates.reward.types.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RewardFactory {

    public static BaseReward create(ConfigurationSection section) {
        String typeRaw = section.getString("type", "ITEM");
        RewardType type = RewardType.valueOf(typeRaw.toUpperCase());

        String name = section.getString("display-name", "Unknown Reward");
        ItemStack displayItem = new RewardItem(section.getString("display-item", "PAPER")).getItemStack();
        double weight = section.getDouble("weight", 1.0);

        return switch (type) {
            case MONEY -> MoneyReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .amount((double)section.getInt("amount", 0))
                    .build();
            case RUBIES -> RubyReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .amount(section.getInt("amount", 0))
                    .build();
            case ITEM -> ItemReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .material(section.getString("material", "PAPER"))
                    .lore(section.getStringList("lore"))
                    .enchantments(section.getStringList("enchantments"))
                    .itemFlags(section.getStringList("item-flags"))
                    .amount(section.getInt("amount", 1))
                    .build();
            case NEXO_ITEM -> NexoItemReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .itemId(section.getString("item-id", "nexo:fallback_item"))
                    .amount(section.getInt("amount", 1))
                    .build();
            case PERMISSION -> PermissionReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .permission(section.getString("permission"))
                    .build();
            case COMMAND -> CommandReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .command(section.getString("command"))
                    .build();
            case CRATE_KEY -> KeyReward.builder()
                    .type(type)
                    .displayName(name)
                    .displayItem(displayItem)
                    .weight(weight)
                    .crateId(section.getString("crate-id"))
                    .amount(section.getInt("amount", 1))
                    .build();
        };
    }
}
