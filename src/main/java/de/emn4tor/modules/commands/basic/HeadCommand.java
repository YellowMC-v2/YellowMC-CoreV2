package de.emn4tor.modules.commands.basic;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.utils.cooldown.CooldownManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HeadCommand implements CommandExecutor {

    private final CooldownManager cooldownManager;
    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public HeadCommand(CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            if (cooldownManager.hasCooldown(player.getUniqueId().toString(), "head")) {
                player.sendMessage(mm.deserialize(
                        "<red>Du kannst aktuell keine Köpfe abholen, warte noch" +
                                " <yellow>" + cooldownManager.getRemainingCooldownFormatted(player.getUniqueId().toString(), "head") + "</yellow>!</red>"
                ));
            } else {
                player.getInventory().addItem(createHead(player));
                player.sendMessage(mm.deserialize("<green>Du hast deinen Kopf erhalten!"));
                cooldownManager.setCooldown(player.getUniqueId().toString(), "head", 24 * 60 * 60 * 3 * 1000); // 3 Tage Cooldown in Millisekunden
            }
            return true;
        }

        if (!player.hasPermission("core.head.other")) {
            player.sendMessage(mm.deserialize("<red>Du hast keine Berechtigung, diesen Befehl zu verwenden!</red>"));
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(mm.deserialize("<red>Spieler nicht gefunden</red>"));
            return true;
        }

        player.getInventory().addItem(createHead(target));
        player.sendMessage(mm.deserialize("<green>Du hast den Kopf von <yellow>" + target.getName() + "</yellow> erhalten!"));
        return true;
    }

    private ItemStack createHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta == null) return head;

        meta.setOwningPlayer(target);
        meta.displayName(mm.deserialize("<green>" + target.getName() + "'s Kopf"));
        meta.lore(List.of(mm.deserialize("<gray>Ausgestellt am <yellow>" + DATE_FORMAT.format(java.time.LocalDateTime.now()) + "</yellow>")));
        head.setItemMeta(meta);

        return head;
    }
}
