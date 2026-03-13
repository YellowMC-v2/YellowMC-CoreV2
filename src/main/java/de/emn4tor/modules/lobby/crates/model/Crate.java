package de.emn4tor.modules.lobby.crates.model;

import de.emn4tor.modules.lobby.crates.reward.BaseReward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
@Builder
public class Crate {
    private final String name;
    private final String displayName;
    private final Location holoGramLocation;
    private final Location crateLocation;
    private final Particle particleEffect;
    private final ItemStack keyItem;
    private final boolean hasPhysicalKey;
    private final boolean hasParticleEffect;
    private final List<BaseReward> rewards;
}
