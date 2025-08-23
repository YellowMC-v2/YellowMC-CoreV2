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
        ranks.put("group.owner", MiniMessage.miniMessage().deserialize("<white>ꚯ</white>"));
        ranks.put("group.admin", MiniMessage.miniMessage().deserialize("<white>ꚮ</white>"));
        ranks.put("group.devop", MiniMessage.miniMessage().deserialize("<white>ꚭ</white>"));
        ranks.put("group.srdeveloper", MiniMessage.miniMessage().deserialize("<white>ꚬ</white>"));
        ranks.put("group.developer", MiniMessage.miniMessage().deserialize("<white>ꚫ</white>"));
        ranks.put("group.mod", MiniMessage.miniMessage().deserialize("<white>ꚪ</white>"));
        ranks.put("group.jrmod", MiniMessage.miniMessage().deserialize("<white>ꚩ</white>"));
        ranks.put("group.support", MiniMessage.miniMessage().deserialize("<white>ꚨ</white>"));
        ranks.put("group.builder", MiniMessage.miniMessage().deserialize("<white>ꚧ</white>"));
        ranks.put("group.designer", MiniMessage.miniMessage().deserialize("<white>ꚦ</white>"));
        ranks.put("group.content", MiniMessage.miniMessage().deserialize("<white>ꚥ</white>"));
        ranks.put("group.platin", MiniMessage.miniMessage().deserialize("<white>ꚤ</white>"));
        ranks.put("group.gold", MiniMessage.miniMessage().deserialize("<white>ꚣ</white>"));
        ranks.put("group.silver", MiniMessage.miniMessage().deserialize("<white>ꚢ</white>"));
        ranks.put("group.bronze", MiniMessage.miniMessage().deserialize("<white>ꚡ</white>"));
        ranks.put("group.default", MiniMessage.miniMessage().deserialize("<white>ꚠ</white>"));



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