package de.emn4tor.modules.spawn;

/*
 *  @author: Emn4tor
 *  @created: 23.08.2025
 */

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class SpawnCommand implements CommandExecutor, Listener {
    private final JavaPlugin plugin;


    public SpawnCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if(args.length == 0){
            player.teleport(getSpawn());
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("set")){
            if (!(sender.hasPermission("mines.setspawn"))){
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Du hast keine Berechtigung, diesen Befehl auszuführen!"));
                return true;
            }
            if (sender instanceof Player) {
                Location loc = player.getLocation();
                saveSpawn(loc);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Spawn gesetzt!"));
            } else {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Dieser Befehl kann nur von einem Spieler ausgeführt werden!"));
            }

        }
        return false;
    }

    private void saveSpawn(Location loc) {
        FileConfiguration config = plugin.getConfig();
        config.set("spawn.world", loc.getWorld().getName());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
    }

    private Location getSpawn() {
        FileConfiguration config = plugin.getConfig();
        String worldName = config.getString("spawn.world");
        double x = config.getDouble("spawn.x");
        double y = config.getDouble("spawn.y");
        double z = config.getDouble("spawn.z");
        float yaw = (float) config.getDouble("spawn.yaw");
        float pitch = (float) config.getDouble("spawn.pitch");

        return new Location(plugin.getServer().getWorld(worldName), x, y, z, yaw, pitch);
    }
}