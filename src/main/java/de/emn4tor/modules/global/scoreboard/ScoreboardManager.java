package de.emn4tor.modules.global.scoreboard;

/*
 *  @author: Emn4tor
 *  @created: 09.04.2025
 */

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.global.economy.rubies.RubyHandler;
import de.emn4tor.modules.global.scoreboard.nametags.RankManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ScoreboardManager implements Listener {

    private final JavaPlugin plugin;
    private final VariableManager vars;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private RankManager rankManager = new RankManager();

    public ScoreboardManager(YellowMCCoreV2 plugin, VariableManager vars) {
        this.plugin = plugin;
        this.vars = vars;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createScoreboardBoard(player);
    }

    public void createScoreboardBoard(Player player) {
        UUID id = player.getUniqueId();

        CompletableFuture<Integer> rubiesFuture = RubyHandler.getRubiesAsync(id);
        CompletableFuture<Integer> playtimeFuture = vars.getPlaytime(id);
        CompletableFuture<Integer> balanceFuture = vars.getBalance(id);

        CompletableFuture.allOf(rubiesFuture, playtimeFuture, balanceFuture).thenRun(() -> {
            int rubies = rubiesFuture.join();
            int playtime = playtimeFuture.join();
            int balance = balanceFuture.join();

            Bukkit.getScheduler().runTask(plugin, () -> {
                Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = board.registerNewObjective("stats", "dummy", mm.deserialize("<glyph:yellowmc_logo_small>"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                createLine(board, obj, 6, " ");
                createLine(board, obj, 5, "<#FFD700>👤 " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-profile") + ": <#FFD700>" + player.getName());
                createLine(board, obj, 4, "<#FCE300>⌚ " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-playtime") + ": <#FCE300>" + formatHours(playtime));
                createLine(board, obj, 3, "<#00FC00>⛃ " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-coins") + ": <#00FC00>" + formatBalance(balance));
                createLine(board, obj, 2, "<#FC0800>💎 " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-rubies") + ": <#FC0800>" + formatRubies(rubies));
                createLine(board, obj, 1, " ");

                // === Apply Nametag Prefix ===


                Team tagTeam = board.getTeam("tag_" + player.getName());
                if (tagTeam == null) {
                    tagTeam = board.registerNewTeam("tag_" + player.getName());
                }
                tagTeam.prefix(rankManager.getPlayerTag(player));
                tagTeam.addEntry(player.getName());

                // === Apply this scoreboard ===

                player.setScoreboard(board);
            });
        });
    }

    public void updateScoreBoard(Player player) {
        UUID id = player.getUniqueId();

        CompletableFuture<Integer> rubiesFuture = RubyHandler.getRubiesAsync(id);
        CompletableFuture<Integer> playtimeFuture = vars.getPlaytime(id);
        CompletableFuture<Integer> balanceFuture = vars.getBalance(id);

        CompletableFuture.allOf(rubiesFuture, playtimeFuture, balanceFuture).thenRun(() -> {
            int rubies = rubiesFuture.join();
            int playtime = playtimeFuture.join();
            int balance = balanceFuture.join();

            Bukkit.getScheduler().runTask(plugin, () -> {
                Scoreboard board = player.getScoreboard();
                if (board == null || board.getObjective("stats") == null) return;

                updateLine(board, 4, "<#FCE300>⌚ " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-playtime") + ": <#FCE300>" + formatHours(playtime));
                updateLine(board, 3, "<#00FC00>⛃ " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-coins") + ": <#00FC00>" + formatBalance(balance));
                updateLine(board, 2, "<#FC0800>💎 " + YellowMCCoreV2.getTranslationService().translate(player.getUniqueId(), "scoreboard-rubies") + ": <#FC0800>" + formatRubies(rubies));

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Team tagTeam = board.getTeam("tag_" + onlinePlayer.getName());
                    if (tagTeam == null) {
                        tagTeam = board.registerNewTeam("tag_" + onlinePlayer.getName());
                    }
                    tagTeam.prefix(rankManager.getPlayerTag(onlinePlayer));
                    tagTeam.addEntry(onlinePlayer.getName());
                }

            });
        });
    }

    private void createLine(Scoreboard board, Objective objective, int score, String text) {
        String entry = getUniqueEntry(score);
        Team team = board.registerNewTeam("line" + score);
        team.addEntry(entry);
        team.prefix(mm.deserialize(text));
        objective.getScore(entry).setScore(score);
    }

    private void updateLine(Scoreboard board, int score, String text) {
        Team team = board.getTeam("line" + score);
        if (team != null) {
            team.prefix(mm.deserialize(text));
        }
    }


    private String getUniqueEntry(int index) {
        return "§" + Integer.toHexString(index);
    }

    private String formatHours(int hours) {
        return hours + "h";
    }

    private String formatBalance(int balance) {
        return balance + "<glyph:coin>";
    }

    public String formatRubies(int amount) {
        return amount + "<glyph:ruby>";
    }
}
