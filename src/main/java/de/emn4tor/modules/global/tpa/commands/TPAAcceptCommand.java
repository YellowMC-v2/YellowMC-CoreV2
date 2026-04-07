package de.emn4tor.modules.global.tpa.commands;

import de.emn4tor.modules.global.tpa.services.TPAService;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.stream.Collectors;

public final class TPAAcceptCommand implements BasicCommand {
    private final TPAService tpaService;

    public TPAAcceptCommand(TPAService tpaService) {
        this.tpaService = tpaService;
    }

    @Override
    public void execute(@NonNull CommandSourceStack stack, @NonNull String[] args) {
        if (!(stack.getExecutor() instanceof Player target)) {
            return;
        }

        if (args.length != 1) {
            target.sendMessage("Nutze /tpaaccept <Spieler>");
            return;
        }

        this.tpaService.acceptRequest(target, args[0]);
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack stack, @NonNull String[] args) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
