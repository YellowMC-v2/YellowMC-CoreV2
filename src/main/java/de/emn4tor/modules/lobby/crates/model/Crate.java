package de.emn4tor.modules.lobby.crates.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class Crate {
    private final String name;
    private final String displayName;
    private final String holoGramLocation;
    private final String crateLocation;
    private final Particle particleEffect;
    private final ItemStack keyItem;
    private final boolean hasPhysicalKey;
    private final boolean hasParticleEffect;
}
