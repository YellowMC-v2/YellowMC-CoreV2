package de.emn4tor.modules.lobby.crates.registry;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.lobby.crates.io.CrateFileLoader;
import de.emn4tor.modules.lobby.crates.model.Crate;
import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import de.emn4tor.modules.lobby.crates.reward.RewardFactory;
import de.emn4tor.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.*;

public class CrateRegistry {
    private final Map<String, Crate> crates = new HashMap<>();
    private final Map<String, Crate> locationCache = new HashMap<>();

    public void loadAll() {
        crates.clear();
        locationCache.clear();

        CrateFileLoader loader = new CrateFileLoader();
        File[] files = loader.getCrateFiles();
        YellowMCCoreV2.getInstance().getLogger().info("Found crate files: " + (files == null ? "null" : files.length));


        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String id = file.getName().replace(".yml", "").toLowerCase();

            List<BaseReward> rewards = new ArrayList<>();
            ConfigurationSection rewardSection = config.getConfigurationSection("rewards");

            if (rewardSection != null) {
                for (String key : rewardSection.getKeys(false)) {
                    ConfigurationSection subSection = rewardSection.getConfigurationSection(key);
                    if (subSection != null) {
                        rewards.add(RewardFactory.create(subSection));
                    }
                }
            }

            Location crateLoc = LocationUtil.deserialize(config.getString("crate-location"));
            YellowMCCoreV2.getInstance().getLogger().info("Parsed crateLoc for " + id + ": " + crateLoc);
            Location holoLoc = LocationUtil.deserialize(config.getString("hologram-location"));

            if (crateLoc == null) {
                continue;
            }

            Crate crate = Crate.builder()
                    .name(id)
                    .displayName(config.getString("display-name"))
                    .crateLocation(crateLoc)
                    .holoGramLocation(holoLoc)
                    .particleEffect(Particle.valueOf(config.getString("particle", "VILLAGER_HAPPY")))
                    .keyItem(config.getItemStack("key-item"))
                    .hasPhysicalKey(config.getBoolean("has-physical-key"))
                    .hasParticleEffect(config.getBoolean("has-particle-effect"))
                    .rewards(rewards)
                    .build();


            crates.put(id, crate);
            locationCache.put(toKey(crateLoc), crate);
        }
    }


    public Optional<Crate> getCrateAt(Location location) {
        if (location == null) return Optional.empty();
        String key = toKey(location);
        YellowMCCoreV2.getInstance().getLogger().info("Looking up key: " + key);
        YellowMCCoreV2.getInstance().getLogger().info("Cache keys: " + locationCache.keySet());
        return Optional.ofNullable(locationCache.get(key));
    }

    public Optional<Crate> getCrate(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(crates.get(id.toLowerCase()));
    }

    public Map<String, Crate> getAllCrates() {
        return Map.copyOf(crates);
    }

    private String toKey(Location loc) {
        return loc.getWorld().getName().toLowerCase() + ":" +
                loc.getBlockX() + ":" +
                loc.getBlockY() + ":" +
                loc.getBlockZ();
    }
}