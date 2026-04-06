package de.emn4tor.modules.smp.homes.services;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.smp.homes.models.Home;
import de.emn4tor.modules.smp.homes.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class HomeService {
    private final HomeRepository homeRepository;
    public static final int MAX_HOMES = 5;

    public @NonNull @Unmodifiable List<Home> findHomesByPlayer(@NotNull Player player) {
        var homes = this.homeRepository.findHomesByUUID(player.getUniqueId());

        return homes.stream().toList();
    }

    public boolean createHome(@NotNull Player player, int homeNumber) {
        if (homeNumber < 1 || homeNumber > MAX_HOMES) {
            return false;
        }

        if (this.homeRepository.findHomeByUUIDAndNumber(player.getUniqueId(), homeNumber).isPresent()) {
            return false;
        }

        var location = player.getLocation();
        var home = Home.builder()
                .uuid(player.getUniqueId())
                .serverID(YellowMCCoreV2.getServerName())
                .homeNumber(homeNumber)
                .worldName(location.getWorld().getName())
                .x(location.getX())
                .y(location.getY())
                .z(location.getZ())
                .yaw(location.getYaw())
                .pitch(location.getPitch())
                .build();

        this.homeRepository.createHome(home);

        return true;
    }

    public Optional<Home> findHomeByPlayerAndNumber(@NotNull Player player, int homeNumber) {
        return this.homeRepository.findHomeByUUIDAndNumber(player.getUniqueId(), homeNumber);
    }

    public boolean deleteHome(@NotNull Player player, int homeNumber) {
        if (this.homeRepository.findHomeByUUIDAndNumber(player.getUniqueId(), homeNumber).isEmpty()) {
            return false;
        }

        this.homeRepository.deleteHome(player.getUniqueId(), homeNumber);
        return true;
    }
}
