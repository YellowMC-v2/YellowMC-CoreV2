package de.emn4tor.modules.global.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.utils.cooldown.CooldownManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignCommand implements CommandExecutor {
    private final CooldownManager cooldownManager;

    public SignCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "sign-empty", FormatService.MessageType.ERROR));
            return false;
        }
        if (player.hasPermission("core.sign")){
            if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "sign")) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "sign-cooldown", FormatService.MessageType.ERROR, Map.of("0", cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "sign"))));
                return true;
            }
            else{
                signItem(player, String.join(" ", args));
                cooldownManager.setCooldown(player.getUniqueId().toString(), "sign", 6 * 60 * 60 * 1000); // 6 hours cooldown
                return true;
            }
        }
        return false;
    }



    private void signItem(Player player, String text) {
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        String prefix = "<dark_gray>Signed by <yellow>" + player.getName() + " on </yellow><gray>" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "</gray>: ";
        lore.add(MiniMessage.miniMessage().deserialize(prefix + text));
        meta.lore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "sign-success", FormatService.MessageType.SYSTEM));
    }
}
