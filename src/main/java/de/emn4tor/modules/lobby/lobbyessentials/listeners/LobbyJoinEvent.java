package de.emn4tor.modules.lobby.lobbyessentials.listeners;

import de.emn4tor.YellowMCCoreV2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class LobbyJoinEvent implements Listener {

    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);
        Location loc = new Location(Bukkit.getWorld("world"), 0, 65, 0, -180, 0);

        player.teleport(loc);
        player.sendMessage(MiniMessage.miniMessage().deserialize("\n             <reset><glyph:yellowmc_logo_small>\n\n            " + formatTime(player) + "\n            <reset>" + randomCallout(player.getUniqueId()) + "\n"));

    }

    private String formatTime(Player player) {
        LocalTime now = LocalTime.now();
        String message;
        UUID playerUUID = player.getUniqueId();

        if (now.getHour() >= 6 && now.getHour() < 12) {
            message = YellowMCCoreV2.getTranslationService().translate(playerUUID, "lobby-greeting-morning", Map.of("0", player.getName()));
        } else if (now.getHour() >= 12 && now.getHour() < 14) {
            message = YellowMCCoreV2.getTranslationService().translate(playerUUID, "lobby-greeting-noon", Map.of("0", player.getName()));
        } else if (now.getHour() >= 14 && now.getHour() < 18) {
            message = YellowMCCoreV2.getTranslationService().translate(playerUUID, "lobby-greeting-afternoon", Map.of("0", player.getName()));
        } else {
            message = YellowMCCoreV2.getTranslationService().translate(playerUUID, "lobby-greeting-night", Map.of("0", player.getName()));
        }
        return message;
    }

    private String randomCallout(UUID uuid) {
        if (YellowMCCoreV2.getLocaleService().getLocale(uuid) == Locale.GERMAN) {
            return deGreet.get((int) (Math.random() * deGreet.size()));
        } else {
            return enGreet.get((int) (Math.random() * enGreet.size()));
        }
    }

    List<String> deGreet = List.of(
            "<yellow>Stürze dich ins Abenteuer",
            "<yellow>Viel Spaß auf dem Server",
            "<yellow>Viel Spaß beim Spielen",
            "<yellow>Genieß die Reise",
            "<yellow>Erlebe epische Momente",
            "<yellow>Geh auf große Entdeckungsreise",
            "<yellow>Finde neue Freunde",
            "<yellow>Mach dich bereit für den Kampf",
            "<yellow>Werde ein Held",
            "<yellow>Zeige deinen Mut",
            "<yellow>Entdecke neue Welten",
            "<yellow>Lass dich von der Magie verzaubern",
            "<yellow>Sei der Champion",
            "<yellow>Schlage das Unmögliche",
            "<yellow>Mach das Unmögliche möglich",
            "<yellow>Ab in die Action",
            "<yellow>Deine Reise beginnt jetzt",
            "<yellow>Finde deinen Weg",
            "<yellow>Stell dich der Herausforderung",
            "<yellow>Erlebe neue Abenteuer",
            "<yellow>Der Spaß wartet auf dich",
            "<yellow>Geh auf Schatzsuche",
            "<yellow>Die Welt ist dein Spielplatz",
            "<yellow>Schreib deine eigene Geschichte",
            "<yellow>Verändere die Welt",
            "<yellow>Lass die Abenteuer beginnen",
            "<yellow>Jage deine Träume",
            "<yellow>Mach dich bereit für das Abenteuer",
            "<yellow>Die nächste Herausforderung wartet",
            "<yellow>Erklimme den Gipfel",
            "<yellow>Entfessle deine Kraft",
            "<yellow>Finde dein Schicksal",
            "<yellow>Du bist der Held der Geschichte",
            "<yellow>Lass das Abenteuer nie enden",
            "<yellow>Spring in die Action",
            "<yellow>Sei unaufhaltbar",
            "<yellow>Dein Abenteuer wartet",
            "<yellow>Erreiche dein Ziel",
            "<yellow>Mach deinen Weg frei",
            "<yellow>Finde neue Möglichkeiten",
            "<yellow>Kein Hindernis ist zu groß"
    );

    List<String> enGreet = List.of(
        "<yellow>Dive into the adventure",
                "<yellow>Have fun on the server",
                "<yellow>Have fun playing",
                "<yellow>Enjoy the journey",
                "<yellow>Experience epic moments",
                "<yellow>Go on a great discovery tour",
                "<yellow>Make new friends",
                "<yellow>Get ready for battle",
                "<yellow>Become a hero",
                "<yellow>Show your courage",
                "<yellow>Explore new worlds",
                "<yellow>Be enchanted by the magic",
                "<yellow>Be the champion",
                "<yellow>Defeat the impossible",
                "<yellow>Make the impossible possible",
                "<yellow>Jump into the action",
                "<yellow>Your journey begins now",
                "<yellow>Find your way",
                "<yellow>Face the challenge",
                "<yellow>Experience new adventures",
                "<yellow>The fun is waiting for you",
                "<yellow>Go on a treasure hunt",
                "<yellow>The world is your playground",
                "<yellow>Write your own story",
                "<yellow>Change the world",
                "<yellow>Let the adventure begin",
                "<yellow>Chase your dreams",
                "<yellow>Get ready for the adventure",
                "<yellow>The next challenge awaits",
                "<yellow>Climb to the summit",
                "<yellow>Unleash your power",
                "<yellow>Find your destiny",
                "<yellow>You are the hero of the story",
                "<yellow>Never let the adventure end",
                "<yellow>Leap into the action",
                "<yellow>Be unstoppable",
                "<yellow>Your adventure awaits",
                "<yellow>Reach your goal",
                "<yellow>Clear your path",
                "<yellow>Find new possibilities",
                "<yellow>No obstacle is too big"
    );



}
