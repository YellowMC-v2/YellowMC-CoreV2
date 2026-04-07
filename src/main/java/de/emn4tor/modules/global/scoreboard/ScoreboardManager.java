package de.emn4tor.modules.global.scoreboard;

import de.emn4tor.YellowMCCoreV2;
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

public class ScoreboardManager implements Listener {

    private final JavaPlugin plugin;
    private final VariableManager vars;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final RankManager rankManager = new RankManager();

    public ScoreboardManager(YellowMCCoreV2 plugin, VariableManager vars) {
        this.plugin = plugin;
        this.vars = vars;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        this.createScoreboardBoard(event.getPlayer());
    }

    public void createScoreboardBoard(Player player) {
        UUID id = player.getUniqueId();

        this.vars.getRubiesAsync(id).thenAccept(rubies -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                int playtime = this.vars.getPlaytime(id);
                double balance = this.vars.getBalance(id);

                Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = board.registerNewObjective("stats", "dummy", this.mm.deserialize("<glyph:yellowmc_logo_small>"));
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);

                this.createLine(board, obj, 6, " ");
                this.createLine(board, obj, 5, "<#FFD700>👤 " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-profile") + ": <#FFD700>" + player.getName());
                this.createLine(board, obj, 4, "<#FCE300>⌚ " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-playtime") + ": <#FCE300>" + this.formatHours(playtime));
                this.createLine(board, obj, 3, "<#00FC00>⛃ " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-coins") + ": <#00FC00>" + this.formatBalance(balance));
                this.createLine(board, obj, 2, "<#FC0800>💎 " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-rubies") + ": <#FC0800>" + this.formatRubies(rubies));
                this.createLine(board, obj, 1, " ");

                this.updateNametags(board);
                player.setScoreboard(board);
            });
        });
    }

    public void updateScoreBoard(Player player) {
        UUID id = player.getUniqueId();
        Scoreboard board = player.getScoreboard();

        if (board == null || board.getObjective("stats") == null) return;

        this.vars.getRubiesAsync(id).thenAccept(rubies -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                int playtime = this.vars.getPlaytime(id);
                double balance = this.vars.getBalance(id);

                this.updateLine(board, 4, "<#FCE300>⌚ " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-playtime") + ": <#FCE300>" + this.formatHours(playtime));
                this.updateLine(board, 3, "<#00FC00>⛃ " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-coins") + ": <#00FC00>" + this.formatBalance(balance));
                this.updateLine(board, 2, "<#FC0800>💎 " + YellowMCCoreV2.getTranslationService().translate(id, "scoreboard-rubies") + ": <#FC0800>" + this.formatRubies(rubies));

                this.updateNametags(board);
            });
        });
    }

    private void updateNametags(Scoreboard board) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String teamName = "tag_" + onlinePlayer.getName();
            Team tagTeam = board.getTeam(teamName);
            if (tagTeam == null) {
                tagTeam = board.registerNewTeam(teamName);
            }
            tagTeam.prefix(this.rankManager.getPlayerTag(onlinePlayer));
            tagTeam.addEntry(onlinePlayer.getName());
        }
    }

    private void createLine(Scoreboard board, Objective objective, int score, String text) {
        String entry = this.getUniqueEntry(score);
        Team team = board.registerNewTeam("line" + score);
        team.addEntry(entry);
        team.prefix(this.mm.deserialize(text));
        objective.getScore(entry).setScore(score);
    }

    private void updateLine(Scoreboard board, int score, String text) {
        Team team = board.getTeam("line" + score);
        if (team != null) {
            team.prefix(this.mm.deserialize(text));
        }
    }

    private String getUniqueEntry(int index) {
        return "§" + Integer.toHexString(index);
    }

    private String formatHours(int hours) {
        return hours + "h";
    }

    private String formatBalance(double balance) {
        return (int) balance + "<glyph:coin>";
    }

    private String formatRubies(int amount) {
        return amount + "<glyph:ruby>";
    }
}