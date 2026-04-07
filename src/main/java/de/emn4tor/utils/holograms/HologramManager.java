package de.emn4tor.utils.holograms;

import de.emn4tor.YellowMCCoreV2;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HologramManager {

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration config;
    private final List<HologramInstance> activeHolograms = new CopyOnWriteArrayList<>();

    private static final double LINE_SPACING = 0.28;
    private static final double VIEW_DISTANCE_SQ = 1024.0;

    public HologramManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "holos.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        loadFromConfig();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(this::updateForPlayer);
        }, 0L, 20L);
    }

    public HologramManager init() {
        loadFromConfig();

        var cmd = plugin.getCommand("hologram");
        if (cmd != null) {
            cmd.setExecutor(new HologramCommand(this));
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                updateForPlayer(event.getPlayer());
            }
        }, plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(this::updateForPlayer);
        }, 0L, 20L);

        return this;
    }

    private void updateLoop(YellowMCCoreV2 plugin, HologramManager holoManager) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                holoManager.updateForPlayer(player);
            }
        }, 0L, 20L);
    }

    public void updateForPlayer(Player player) {
        Location pLoc = player.getLocation();
        for (HologramInstance holo : activeHolograms) {
            if (holo.getCenterLocation().getWorld().equals(pLoc.getWorld())
                    && holo.getCenterLocation().distanceSquared(pLoc) < VIEW_DISTANCE_SQ) {
                holo.show(player);
            } else {
                holo.hide(player);
            }
        }
    }

    public void createHolo(Location loc, List<String> lines, boolean save) {
        List<ProtocolLine> protocolLines = new ArrayList<>();
        Location current = loc.clone();

        for (String text : lines) {
            protocolLines.add(new ProtocolLine(current.clone(), text));
            current.subtract(0, LINE_SPACING, 0);
        }

        activeHolograms.add(new HologramInstance(protocolLines));

        if (save) {
            String id = UUID.randomUUID().toString().split("-")[0];
            config.set("holograms." + id + ".location", loc);
            config.set("holograms." + id + ".lines", lines);
            saveConfig();
        }
    }

    /**
     * Removes the hologram closest to a location (within 2 blocks)
     */
    public boolean removeHoloAt(Location loc) {
        HologramInstance target = activeHolograms.stream()
                .filter(h -> h.getCenterLocation().distanceSquared(loc) < 4.0)
                .findFirst().orElse(null);

        if (target != null) {
            // Remove from packets for everyone currently seeing it
            Bukkit.getOnlinePlayers().forEach(target::hide);
            activeHolograms.remove(target);

            // Remove from YML (find by location match)
            ConfigurationSection section = config.getConfigurationSection("holograms");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    Location savedLoc = section.getLocation(key + ".location");
                    if (savedLoc != null && savedLoc.distanceSquared(loc) < 1.0) {
                        config.set("holograms." + key, null);
                        saveConfig();
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void loadFromConfig() {
        ConfigurationSection section = config.getConfigurationSection("holograms");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Location loc = section.getLocation(key + ".location");
            List<String> lines = section.getStringList(key + ".lines");
            if (loc != null) createHolo(loc, lines, false);
        }
    }

    @SneakyThrows
    private void saveConfig() {
        config.save(file);
    }
}