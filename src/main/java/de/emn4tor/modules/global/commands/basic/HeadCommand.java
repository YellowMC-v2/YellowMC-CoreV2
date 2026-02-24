package de.emn4tor.modules.global.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.utils.cooldown.CooldownManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class HeadCommand implements CommandExecutor {

    private final CooldownManager cooldownManager;
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public HeadCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "head")) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "head-self-cooldown", FormatService.MessageType.ERROR, Map.of("0", cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "head"))));
            } else {
                player.getInventory().addItem(createHead(player));
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "head-self-success", FormatService.MessageType.SYSTEM));
                cooldownManager.setCooldown(player.getUniqueId().toString(), "head", 24 * 60 * 60 * 3 * 1000); // 3 Tage Cooldown in Millisekunden
            }
            return true;
        }

        if (!player.hasPermission("core.head.other")) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "head-other-permission", FormatService.MessageType.ERROR));
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
            return true;
        }

        player.getInventory().addItem(createHead(target));
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "head-other-success", FormatService.MessageType.SYSTEM, Map.of("0", target.getName())));
        return true;
    }

    private ItemStack createHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta == null) return head;

        meta.setOwningPlayer(target);
        meta.displayName(mm.deserialize("<green>" + target.getName() + "'s Head"));
        meta.lore(List.of(mm.deserialize("<gray>Created on: <yellow>" + DATE_FORMAT.format(java.time.LocalDateTime.now()) + "</yellow>")));
        head.setItemMeta(meta);

        return head;
    }
}
