package de.emn4tor.modules.daily;

/*
 *  @author: Emn4tor
 *  @created: 12.06.2025
 */

import de.emn4tor.modules.daily.enums.RankType;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class RankGetter {
    private final LinkedHashMap<String, RankType> ranks = new LinkedHashMap<>();

    public RankGetter() {
        ranks.put("group.platin", RankType.PLATIN);
        ranks.put("group.gold", RankType.GOLD);
        ranks.put("group.silver", RankType.SILVER);
        ranks.put("group.bronze", RankType.BRONZE);
        ranks.put("group.default", RankType.DEFAULT);
    }

    public RankType getPlayerRankType(Player player) {
        for (Map.Entry<String, RankType> entry : ranks.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                return entry.getValue();
            }
        }
        return ranks.get("group.default");
    }
}
