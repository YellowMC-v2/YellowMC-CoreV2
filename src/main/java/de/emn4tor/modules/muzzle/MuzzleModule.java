package de.emn4tor.modules.muzzle;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.muzzle.bans.BanCommand;
import de.emn4tor.modules.muzzle.bans.BanManager;
import de.emn4tor.modules.muzzle.bans.KickCommand;
import de.emn4tor.modules.muzzle.mute.CheckMuteCommand;
import de.emn4tor.modules.muzzle.mute.MuteCommand;
import de.emn4tor.modules.muzzle.mute.UnmuteCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MuzzleModule implements Module {
    public static final Set<String> bannedWords = new HashSet<>();
    private ChatManager chatManager;
    private BanManager banManager;


    @Override
    public String getName() {
        return "MuzzleModule (Chat & Bansystem)";
    }

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        //Register word list
        registerWordList(plugin);
        chatManager = new ChatManager();
        banManager = new BanManager();
        plugin.getCommand("cleatchat").setExecutor(new ClearChatCommand(chatManager));
        plugin.getCommand("mute").setExecutor(new MuteCommand(plugin));
        plugin.getCommand("unmute").setExecutor(new UnmuteCommand(plugin));
        plugin.getCommand("kick").setExecutor(new KickCommand(banManager, plugin));
        plugin.getCommand("ban").setExecutor(new BanCommand(banManager, plugin));
        plugin.getCommand("checkmute").setExecutor(new CheckMuteCommand(plugin));

    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {
        if (chatManager != null) {
            chatManager.shutdown();
        }
    }

    private void registerWordList(YellowMCCoreV2 plugin){
        File file = new File(plugin.getDataFolder(), "banned_words.txt");
        if (!file.exists()) {
            plugin.saveResource("banned_words.txt", false); // copy default if not exists
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bannedWords.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load banned words: " + e.getMessage());
        }
    }
}
