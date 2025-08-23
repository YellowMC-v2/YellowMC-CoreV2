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

public class FeedCommand implements CommandExecutor {
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final CooldownManager cooldownManager;

    public FeedCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return false;

        if (args.length == 1) {
            if (!player.hasPermission("core.feed.others")) {
                player.sendMessage(mm.deserialize("<red>Du hast keine Berechtigung, andere Spieler zu füttern!</red>"));
                return true;
            }
            if (args[0].equalsIgnoreCase("all")) {
                for (Player p : player.getServer().getOnlinePlayers()) {
                    p.setFoodLevel(20);
                    p.setSaturation(20);
                    p.sendMessage(mm.deserialize("<green>Du wurdest gefüttert!</green>"));
                }
                return true;
            }

            Player target = player.getServer().getPlayer(args[0]);
            if (target != null) {
                target.setFoodLevel(20);
                target.setSaturation(20);
                target.sendMessage(mm.deserialize("<green>Du wurdest gefüttert!</green>"));
                player.sendMessage(mm.deserialize("<green>Du hast <yellow>" + target.getName() + "</yellow> gefüttert!</green>"));
            } else {
                player.sendMessage(mm.deserialize("<red>Der Spieler ist nicht online!</red>"));
            }
            return true;
        }
        if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "feed")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Du kannst aktuell nicht gefüttert werden, warte noch <yellow>" + cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "feed") + "<red>!"));
            return true;
        }
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.sendMessage(mm.deserialize("<green>Du wurdest gefüttert!</green>"));
        cooldownManager.setCooldown(player.getUniqueId().toString(), "feed", 15 * 60 * 1000); // 15 minutes cooldown
        return true;
    }
}
