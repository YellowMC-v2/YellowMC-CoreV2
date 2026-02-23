package de.emn4tor.modules.commands;

/*
 *  @author: Emn4tor
 *  @created: 21.08.2025
 */

import de.emn4tor.Module;
import de.emn4tor.ModuleInfo;
import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.commands.admin.FlySpeedCommand;
import de.emn4tor.modules.commands.admin.GamemodeCommand;
import de.emn4tor.modules.commands.basic.*;
import de.emn4tor.modules.commands.languagesel.InvCloseListener;
import de.emn4tor.modules.commands.languagesel.LanguageCommand;
import de.emn4tor.modules.commands.languagesel.LanguageInventoryListener;
import de.emn4tor.modules.commands.social.DiscordCommand;
import de.emn4tor.modules.commands.social.InstagramCommand;
import de.emn4tor.modules.commands.social.TikTokCommand;
import de.emn4tor.modules.commands.social.YouTubeCommand;
import de.emn4tor.modules.commands.workstations.*;
import de.emn4tor.utils.cooldown.CooldownManager;

@ModuleInfo(name = "CommandsModule")
public class CommandsModule implements Module {
    private CooldownManager cooldownManager;

    @Override
    public void onEnable(YellowMCCoreV2 plugin) {
        cooldownManager = new CooldownManager(YellowMCCoreV2.getRedisManager());
        LanguageCommand languageCommand = new LanguageCommand();
        //Admin Commands
        plugin.getCommand("gamemode").setExecutor(new GamemodeCommand());
        plugin.getCommand("flyspeed").setExecutor(new FlySpeedCommand());
        //basic commands
        plugin.getCommand("feed").setExecutor(new FeedCommand(cooldownManager));
        plugin.getCommand("fly").setExecutor(new FlyCommand());
        plugin.getCommand("hat").setExecutor(new HatCommand());
        plugin.getCommand("head").setExecutor(new HeadCommand(cooldownManager));
        plugin.getCommand("heal").setExecutor(new HealCommand(cooldownManager));
        plugin.getCommand("sign").setExecutor(new SignCommand(cooldownManager));
        // social commands
        plugin.getCommand("discord").setExecutor(new DiscordCommand());
        plugin.getCommand("tiktok").setExecutor(new TikTokCommand());
        plugin.getCommand("youtube").setExecutor(new YouTubeCommand());
        plugin.getCommand("instagram").setExecutor(new InstagramCommand());
        // workstation commands
        plugin.getCommand("anvil").setExecutor(new AnvilCommand());
        plugin.getCommand("craft").setExecutor(new CraftingCommand());
        plugin.getCommand("enderchest").setExecutor(new EnderChestCommand());
        plugin.getCommand("loom").setExecutor(new LoomCommand());
        plugin.getCommand("smithingtable").setExecutor(new SmithingTableCommand());
        plugin.getCommand("stonecutter").setExecutor(new StoneCutterCommand());

        plugin.getCommand("language").setExecutor(languageCommand);

        plugin.getServer().getPluginManager().registerEvents(new LanguageInventoryListener(languageCommand), plugin);
        plugin.getServer().getPluginManager().registerEvents(new InvCloseListener(), plugin);
    }

    @Override
    public void onDisable(YellowMCCoreV2 plugin) {

    }
}
