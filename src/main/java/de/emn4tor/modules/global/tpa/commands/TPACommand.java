package de.emn4tor.modules.global.tpa.commands;

import de.emn4tor.modules.global.tpa.services.TPAService;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class TPACommand implements BasicCommand {
    private final TPAService tpaService;

    public TPACommand(TPAService tpaService) {
        this.tpaService = tpaService;
    }

    @Override
    public void execute(@NonNull CommandSourceStack stack, @NonNull String[] args) {
        if (!(stack.getExecutor() instanceof Player sender)) {
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("Nutze /tpa <Spieler>");
            return;
        }

        var targetName = args[0];

        if (targetName.equalsIgnoreCase(sender.getName())) {
            sender.sendMessage("Du kannst dir nicht selbst eine Anfrage schicken.");
            return;
        }

        this.tpaService.sendRequest(sender, targetName);
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack stack, @NonNull String[] args) {
        if (args.length <= 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
