package de.emn4tor.modules.lobby.lobbyessentials;

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.commons.ServerConnector;
import de.emn4tor.modules.lobby.lobbyessentials.commands.QuickMenu;
import de.emn4tor.modules.lobby.lobbyessentials.commands.ToggleBuildMode;
import de.emn4tor.modules.lobby.lobbyessentials.listeners.CanceledEvents;
import de.emn4tor.modules.lobby.lobbyessentials.listeners.LobbyJoinEvent;
import de.emn4tor.modules.lobby.lobbyessentials.listeners.OnPlayerMoveEvent;
import de.emn4tor.modules.lobby.lobbyessentials.listeners.QuitEvent;

@ModuleInfo(name = "LobbyModule", server = "lobby")
public class LobbyModule implements Module {
    private ServerConnector serverConnector;


    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CanceledEvents(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LobbyJoinEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new QuitEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new OnPlayerMoveEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new QuickMenu(), plugin);

        plugin.getCommand("buildmode").setExecutor(new ToggleBuildMode());
        plugin.getCommand("quickmenu").setExecutor(new QuickMenu());

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        serverConnector = new ServerConnector(plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
