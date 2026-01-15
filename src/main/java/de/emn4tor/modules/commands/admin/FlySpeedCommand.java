package de.emn4tor.modules.commands.admin;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlySpeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length == 0) {
            player.sendRichMessage("<red>Verwendung: /flyspeed <Geschwindigkeit>");
            return true;
        }

        float speed;
        try {
            speed = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            player.sendRichMessage("<red>Ungültige Zahl.");
            return true;
        }

        if (speed < 0 || speed > 10) {
            player.sendRichMessage("<red>Die Geschwindigkeit muss zwischen 0 und 10 liegen.");
            return true;
        }

        player.setFlySpeed(speed / 10f);
        player.sendRichMessage("<green>Fluggeschwindigkeit auf <bold>" + speed + "</bold> gesetzt.");

        return true;
    }
}
