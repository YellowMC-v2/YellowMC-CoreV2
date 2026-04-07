package de.emn4tor.modules.global.daily;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.api.FormatService;
import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.global.economy.coins.api.services.CoinService;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Map;

public class DailyManager {

    private final RankGetter rankGetter;
    private final CoinService coinService;

    public DailyManager(RankGetter rankGetter, CoinService coinService) {
        this.rankGetter = rankGetter;
        this.coinService = coinService;
        this.setupDatabase();
    }

    public void giveDailyReward(Player player) {
        var streak = this.updateStreak(player);
        var type = this.rankGetter.getPlayerRankType(player);

        var rubies = type.getRubies();
        var coins = type.getCoins();

        this.coinService.addCoins(player.getUniqueId(), coins);
        RubyHandler.addRubies(player.getUniqueId(), rubies);

        player.sendRichMessage(
                YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "daily-join-msg-title") +
                        "    <red>" + rubies + " 💎 <gray>| <gold>" + coins + " 🪙 <gray>| <color:#ff2200>" + streak + "🔥 Streak"
        );

        var actionBarMsg = YellowMCCoreV2.getMessageService().sendMessage(
                player.getUniqueId(), "daily-join-msg-actionbar",
                FormatService.MessageType.SYSTEM, Map.of("0", String.valueOf(streak))
        );

        player.sendActionBar(MiniMessage.miniMessage().deserialize(actionBarMsg));
    }

    public int updateStreak(Player player) {
        var id = player.getUniqueId();
        var select = "SELECT last_claimed, streak FROM daily_cooldown WHERE uuid = ?";
        var insert = "INSERT INTO daily_cooldown (uuid, last_claimed, streak) VALUES (?, NOW(), 1)";
        var update = "UPDATE daily_cooldown SET last_claimed = NOW(), streak = ? WHERE uuid = ?";

        try (var conn = SQLManager.getInstance().getConnection();
             var stmtSelect = conn.prepareStatement(select)) {

            stmtSelect.setString(1, id.toString());
            try (var rs = stmtSelect.executeQuery()) {
                if (rs.next()) {
                    var last = rs.getTimestamp("last_claimed").getTime();
                    var streak = rs.getInt("streak");
                    var now = System.currentTimeMillis();

                    var withinRange = (now - last) < 48L * 60 * 60 * 1000;
                    var newStreak = withinRange ? streak + 1 : 1;

                    try (var stmtUpdate = conn.prepareStatement(update)) {
                        stmtUpdate.setInt(1, newStreak);
                        stmtUpdate.setString(2, id.toString());
                        stmtUpdate.executeUpdate();
                    }
                    return newStreak;
                } else {
                    try (var stmtInsert = conn.prepareStatement(insert)) {
                        stmtInsert.setString(1, id.toString());
                        stmtInsert.executeUpdate();
                    }
                    return 1;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 1;
    }

    public boolean isOnCooldown(Player player) {
        var query = "SELECT last_claimed FROM daily_cooldown WHERE uuid = ?";
        try (var conn = SQLManager.getInstance().getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, player.getUniqueId().toString());
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var lastClaimed = rs.getTimestamp("last_claimed").getTime();
                    return (System.currentTimeMillis() - lastClaimed) < 24L * 60 * 60 * 1000;
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void setupDatabase() {
        var createTable = """
            CREATE TABLE IF NOT EXISTS daily_cooldown (
                uuid VARCHAR(36) PRIMARY KEY,
                last_claimed TIMESTAMP,
                streak INT DEFAULT 1
            )
        """;
        try (var conn = SQLManager.getInstance().getConnection();
             var stmt = conn.prepareStatement(createTable)) {
            stmt.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}