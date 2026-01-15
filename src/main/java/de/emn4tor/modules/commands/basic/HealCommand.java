package de.emn4tor.modules.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.utils.cooldown.CooldownManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
                player.sendMessage(mm.deserialize("<red>Du hast keine Berechtigung, andere Spieler zu heilen!</red>"));
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    p.setHealth(20);
                    p.setFoodLevel(20);
                    p.sendMessage(mm.deserialize("<green>Du wurdest geheilt!</green>"));
                }
                return true;
            }

            Player target = player.getServer().getPlayer(args[0]);
            if (target != null) {
                target.setHealth(20);
                target.setFoodLevel(20);
                target.sendMessage(mm.deserialize("<green>Du wurdest geheilt!</green>"));
                player.sendMessage(mm.deserialize("<green>Du hast <yellow>" + target.getName() + "</yellow> geheilt!</green>"));            } else {
                player.sendMessage(mm.deserialize("<red>Der Spieler ist nicht online!</red>"));
            }
            return true;
        }
        if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "heal")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Du kannst aktuell nicht geheilt werden, warte noch <yellow>" + cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "heal") + "<red>!"));
            return true;
        }

        player.setHealth(20);
        player.setFoodLevel(20);
        player.sendMessage(mm.deserialize("<green>Du wurdest geheilt!</green>"));
        cooldownManager.setCooldown(player.getUniqueId().toString(), "heal", 40 * 60 * 1000); // 40 minutes cooldown
        return true;
    }
}
