package de.emn4tor.modules.economy.rubies;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RubiesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<red>Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        showOwnRubies(player);
        return true;
    }

    private void showOwnRubies(Player player) {
        RubyHandler.getRubiesAsync(player.getUniqueId()).thenAccept(rubies ->
                player.sendRichMessage("<yellow>Du hast <green>" + rubies + " <yellow>Rubine.")
        );
    }
}