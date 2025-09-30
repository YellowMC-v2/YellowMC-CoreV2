package de.emn4tor.modules.scoreboard.nametags;

/*
 *  @author: Emn4tor
 *  @created: 28.05.2025
 */

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;


public class RankManager {
    private final LinkedHashMap<String, Component> ranks = new LinkedHashMap<>();


    public RankManager() {
        ranks.put("group.owner", MiniMessage.miniMessage().deserialize("<glyph:ranks17>"));
        ranks.put("group.admin", MiniMessage.miniMessage().deserialize("<glyph:ranks16>"));
        ranks.put("group.devop", MiniMessage.miniMessage().deserialize("<glyph:ranks15>"));
        ranks.put("group.srdeveloper", MiniMessage.miniMessage().deserialize("<glyph:ranks14>"));
        ranks.put("group.developer", MiniMessage.miniMessage().deserialize("<glyph:ranks11>"));
        ranks.put("group.mod", MiniMessage.miniMessage().deserialize("<glyph:ranks13>"));
        ranks.put("group.jrmod", MiniMessage.miniMessage().deserialize("<glyph:ranks12>"));
        ranks.put("group.support", MiniMessage.miniMessage().deserialize("<glyph:ranks7>"));
        ranks.put("group.builder", MiniMessage.miniMessage().deserialize("<glyph:ranks8>"));
        ranks.put("group.designer", MiniMessage.miniMessage().deserialize("<glyph:ranks10>"));
        ranks.put("group.content", MiniMessage.miniMessage().deserialize("<glyph:ranks6>"));
        ranks.put("group.platin", MiniMessage.miniMessage().deserialize("<glyph:ranks5>"));
        ranks.put("group.gold", MiniMessage.miniMessage().deserialize("<glyph:ranks4>"));
        ranks.put("group.silver", MiniMessage.miniMessage().deserialize("<glyph:ranks3>"));
        ranks.put("group.bronze", MiniMessage.miniMessage().deserialize("<glyph:ranks2>"));
        ranks.put("group.default", MiniMessage.miniMessage().deserialize("<glyph:ranks1>"));



    }

    public Component getPlayerTag(Player player) {
        if (player.hasPermission("core.ranks.all")) {
            return ranks.get("group.owner");
        }
        for (Map.Entry<String, Component> entry : ranks.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                return entry.getValue();
            }
        }
        return ranks.get("group.default");
    }


}