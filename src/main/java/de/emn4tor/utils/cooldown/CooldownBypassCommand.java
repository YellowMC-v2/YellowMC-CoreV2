package de.emn4tor.utils.cooldown;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.data.RedisManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CooldownBypassCommand implements CommandExecutor {
    private final RedisManager redisManager;

    public CooldownBypassCommand(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            redisManager.setTemporary(
                    "cooldown_bypass:" + player.getUniqueId(),
                    "true",
                    60 * 5 // 5 minutes in seconds
            );
        }
        else if (args.length == 1) {
            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden.");
                return true;
            }
            redisManager.setTemporary(
                    "cooldown_bypass:" + target.getUniqueId(),
                    "true",
                    60 * 5 // 5 minutes in seconds
            );
            sender.sendMessage("§aCooldown-Bypass für " + target.getName() + " gesetzt.");
        } else {
            sender.sendMessage("§cUsage: /cooldownbypass [spieler]");
            return true;
        }
        return true;
    }
}
