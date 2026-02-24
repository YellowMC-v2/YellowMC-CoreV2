package de.emn4tor.modules.global.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.utils.cooldown.CooldownManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class HealCommand implements CommandExecutor {
    private final CooldownManager cooldownManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public HealCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 1) {
            if (!player.hasPermission("core.heal.others")) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-other-permission", FormatService.MessageType.ERROR));
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-self", FormatService.MessageType.SYSTEM));
                }
                return true;
            }

            Player target = player.getServer().getPlayer(args[0]);
            if (target != null) {
                target.setHealth(20);
                target.setFoodLevel(20);
                target.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-self", FormatService.MessageType.SYSTEM));
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-other", FormatService.MessageType.SYSTEM, Map.of("0", target.getName())));
            } else {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
            }
            return true;
        }
        if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "heal")) {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-cooldown", FormatService.MessageType.ERROR, Map.of("0", cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "heal"))));
            return true;
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "heal-self", FormatService.MessageType.SYSTEM));
        cooldownManager.setCooldown(player.getUniqueId().toString(), "heal", 40 * 60 * 1000); // 40 minutes cooldown
        return true;
    }
}
