package de.emn4tor.modules.global.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            toggleFly(player);
        } else if (args.length == 1){
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "error-target-not-online", FormatService.MessageType.ERROR));
                return true;
            }
            toggleFly(target);
        }
        else {
            player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "fly-usage", FormatService.MessageType.SYSTEM));
            return true;
        }
        return false;
    }


    private void toggleFly(Player player) {
        boolean flying = !player.getAllowFlight();
        player.setAllowFlight(flying);
        player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "fly-self-enabled", FormatService.MessageType.SYSTEM, Map.of("0", flying ? "true" : "false")));

        if (!flying) {
            player.setFlying(false);
        }
    }
}
