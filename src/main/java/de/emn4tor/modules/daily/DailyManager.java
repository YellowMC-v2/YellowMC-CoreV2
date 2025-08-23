package de.emn4tor.modules.daily;

/*
 *  @author: Emn4tor
 *  @created: 12.06.2025
 */

import de.emn4tor.data.SQLManager;
import de.emn4tor.modules.daily.enums.RankType;
import de.emn4tor.modules.economy.coins.api.EconomyHandler;
import de.emn4tor.modules.economy.rubies.RubyHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class DailyManager {
    private final RankGetter rankGetter;

    public DailyManager(RankGetter rankGetter) {
        this.rankGetter = rankGetter;
        setupDatabase();
    }

    public void giveDailyReward(Player player) {
        int streak = updateStreak(player);
        RankType type = rankGetter.getPlayerRankType(player);
        int rubies = type.getRubies();
        int coins = type.getCoins();

        EconomyHandler.addCoins(player, coins);
        RubyHandler.addRubies(player.getUniqueId(), rubies);

        player.sendRichMessage(
                "<#32CD32>🎁 Tägliche Belohnung eingesackt!</#32CD32>\n" +
                        "        <red>" + rubies + " 💎 <gray>| <gold>" + coins + " 🪙 <gray>| <color:#ff2200>" + streak + "🔥 Streak"
        );

        player.sendActionBar(MiniMessage.miniMessage().deserialize(
                "<green><bold>+ Belohnung erhalten! 🔥 Streak: " + streak + " Tage</bold></green>"
        ));
    }

    public int updateStreak(Player player) {
        UUID id = player.getUniqueId();
        String select = "SELECT last_claimed, streak FROM daily_cooldown WHERE uuid = ?";
        String insert = "INSERT INTO daily_cooldown (uuid, last_claimed, streak) VALUES (?, NOW(), 1)";
        String update = "UPDATE daily_cooldown SET last_claimed = NOW(), streak = ? WHERE uuid = ?";

        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(select)) {

            stmtSelect.setString(1, id.toString());
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                long last = rs.getTimestamp("last_claimed").getTime();
                int streak = rs.getInt("streak");
                long now = System.currentTimeMillis();
                boolean withinRange = (now - last) < 48 * 60 * 60 * 1000;

                streak = withinRange ? streak + 1 : 1;

                try (PreparedStatement stmtUpdate = conn.prepareStatement(update)) {
                    stmtUpdate.setInt(1, streak);
                    stmtUpdate.setString(2, id.toString());
                    stmtUpdate.executeUpdate();
                }

                return streak;

            } else {
                try (PreparedStatement stmtInsert = conn.prepareStatement(insert)) {
                    stmtInsert.setString(1, id.toString());
                    stmtInsert.executeUpdate();
                }
                return 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public boolean isOnCooldown(Player player) {
        UUID id = player.getUniqueId();
        String query = "SELECT last_claimed FROM daily_cooldown WHERE uuid = ?";
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id.toString());
            var rs = stmt.executeQuery();
            if (rs.next()) {
                long lastClaimed = rs.getTimestamp("last_claimed").getTime();
                long currentTime = System.currentTimeMillis();
                return (currentTime - lastClaimed) < 24 * 60 * 60 * 1000;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setupDatabase() {
        String createTable = """
            CREATE TABLE IF NOT EXISTS daily_cooldown (
                uuid VARCHAR(36) PRIMARY KEY,
                last_claimed TIMESTAMP,
                streak INT DEFAULT 1
            )
        """;
        try (Connection conn = SQLManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(createTable)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}