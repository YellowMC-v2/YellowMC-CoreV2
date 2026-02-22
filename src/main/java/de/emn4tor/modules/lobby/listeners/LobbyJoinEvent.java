package de.emn4tor.modules.lobby.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalTime;

public class LobbyJoinEvent implements Listener {

    @EventHandler
    public void onJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);
        Location loc = new Location(Bukkit.getWorld("world"), 0, 65, 0, -180, 0);

        player.teleport(loc);
        player.sendMessage(MiniMessage.miniMessage().deserialize("\n             <reset><glyph:yellowmc_logo_small>\n\n            " + formatTime(player) + "\n            <reset>" + randomCallout()));

    }

    private String formatTime(Player player) {
        LocalTime now = LocalTime.now();
        String message;

        if (now.getHour() >= 6 && now.getHour() < 12) {
            message = "<yellow>Guten Morgen, " + player.getName() + "!";
        } else if (now.getHour() >= 12 && now.getHour() < 14) {
            message = "<yellow>Guten Mittag, " + player.getName() + "!";
        } else if (now.getHour() >= 14 && now.getHour() < 18) {
            message = "<yellow>Guten Nachmittag, " + player.getName() + "!";
        } else {
            message = "<yellow>Guten Abend, " + player.getName() + "!";
        }
        return message;
    }

    private String randomCallout() {
        String[] callouts = {
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
                "<yellow>Kein Hindernis ist zu groß",
        };
        return callouts[(int) (Math.random() * callouts.length)];
    }

}
