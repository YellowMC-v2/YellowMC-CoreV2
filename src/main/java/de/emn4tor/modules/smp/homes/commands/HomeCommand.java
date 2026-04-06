package de.emn4tor.modules.smp.homes.commands;

import de.emn4tor.modules.smp.homes.inventories.HomeInventory;
import de.emn4tor.modules.smp.homes.services.HomeService;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class HomeCommand implements BasicCommand {
    private final HomeService homeService;

    @Override
    public void execute(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) {
            return;
        }

        player.sendMessage("JA");

        new HomeInventory(this.homeService).openInventory(player);
    }
}
