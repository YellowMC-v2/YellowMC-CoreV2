package de.emn4tor.modules.global.redeemables;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GetRedeemableCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if (args.length == 0) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Nutze /redeemable <yellow><coins|rubies></yellow> <amount> [<player>]</red>"));
        } else {
            NamespacedKey key = new NamespacedKey("redeemable", "amount");

            if (args[0].equalsIgnoreCase("coins")) {
                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setCustomModelData(100);
                String amount = args.length > 1 ? args[1] : "1";
                itemMeta.displayName(MiniMessage.miniMessage().deserialize("ꑻ<yellow> " + amount + "</yellow>"));
                itemMeta.lore(new ArrayList<>() {{
                    add(MiniMessage.miniMessage().deserialize("<gray>Ein Gutschein für " + amount + " Münzen</gray>"));
                    add(MiniMessage.miniMessage().deserialize("<gray>Rechtsklick um einzulösen</gray>"));
                }});
                itemMeta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.INTEGER, Integer.parseInt(amount));
                itemStack.setItemMeta(itemMeta);
                giveItem(player, itemStack, args);
            }
            else if (args[0].equalsIgnoreCase("rubies")) {
                ItemStack itemStack = new ItemStack(Material.PAPER);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setCustomModelData(101);
                String amount = args.length > 1 ? args[1] : "1";
                itemMeta.displayName(MiniMessage.miniMessage().deserialize("ꑺ<red> " + amount + "</red>"));
                itemMeta.lore(new ArrayList<>() {{
                    add(MiniMessage.miniMessage().deserialize("<gray>Vouch for " + amount + " Rubies"));
                    add(MiniMessage.miniMessage().deserialize("<gray>Right click to use</gray>"));
                }});
                itemMeta.getPersistentDataContainer().set(key, org.bukkit.persistence.PersistentDataType.INTEGER, Integer.parseInt(amount));
                itemStack.setItemMeta(itemMeta);
                giveItem(player, itemStack, args);
            } else {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "redeemable-usabe", FormatService.MessageType.ERROR));
            }

        }
        return true;
    }

    private void giveItem(Player player, ItemStack itemStack, String[] args) {
        if (args.length > 2) {
            Player target = player.getServer().getPlayer(args[2]);
            if (target != null && target.isOnline()) {
                target.getInventory().addItem(itemStack);
                target.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(target.getUniqueId(), "redeemable-receive-self", FormatService.MessageType.SYSTEM));
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "redeemable-give-success", FormatService.MessageType.SYSTEM));
            } else {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
            }
        } else {
            player.getInventory().addItem(itemStack);
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "redeemable-receive-self", FormatService.MessageType.SYSTEM));
        }
    }
}
