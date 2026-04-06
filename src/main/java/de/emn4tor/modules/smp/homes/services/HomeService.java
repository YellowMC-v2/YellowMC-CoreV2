package de.emn4tor.modules.smp.homes.services;

import de.emn4tor.modules.smp.homes.models.Home;
import de.emn4tor.modules.smp.homes.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;

@RequiredArgsConstructor
public final class HomeService {
    private final HomeRepository homeRepository;

    public @NonNull @Unmodifiable List<Home> findHomesByPlayer(@NotNull Player player) {
        var homes = this.homeRepository.findHomesByUUID(player.getUniqueId());

        return homes.stream().toList();
    }

    public void createHome(@NotNull Player player) {

    }
}
