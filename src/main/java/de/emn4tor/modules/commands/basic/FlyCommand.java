package de.emn4tor.modules.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            toggleFly(player);
        } else if (args.length == 1){
            Player target = player.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Spieler nicht gefunden</red>"));
                return true;
            }
            toggleFly(target);
        }
        else {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Nutze: /fly [spieler]</red>"));
            return true;
        }
        return false;
    }


    private void toggleFly(Player player) {
        boolean flying = !player.getAllowFlight();
        player.setAllowFlight(flying);

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<yellow>Flugmodus " + (flying ? "<green>aktiviert" : "<red>deaktiviert") + "</yellow>"
        ));

        if (!flying) {
            player.setFlying(false);
        }
    }
}
