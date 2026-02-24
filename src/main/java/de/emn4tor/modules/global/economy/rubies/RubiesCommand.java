package de.emn4tor.modules.global.economy.rubies;

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
                player.sendRichMessage(YellowMCCoreV2.getMessageService().sendMessage(player.getUniqueId(), "ruby-balance-self", FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(rubies))))
        );
    }
}