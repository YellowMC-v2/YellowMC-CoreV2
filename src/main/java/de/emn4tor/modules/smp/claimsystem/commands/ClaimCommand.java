package de.emn4tor.modules.smp.claimsystem.commands;

import de.emn4tor.YellowMCCoreV2;
import de.emn4tor.modules.smp.claimsystem.logic.ClaimManager;
import de.emn4tor.modules.smp.claimsystem.logic.ClaimParticles;
import de.emn4tor.modules.smp.claimsystem.models.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the /claim command, including subcommands like trust, untrust, delete, reload, and showborder.
 * Provides tab-completion for command arguments and validates permissions and ownership.
 */
public class ClaimCommand implements CommandExecutor, TabCompleter {

    private final ClaimManager claimManager;
    private final Set<String> adminCommands = Set.of("reload");

    /**
     * Constructs a ClaimCommand instance with the provided ClaimManager.
     *
     * @param claimManager the ClaimManager responsible for managing claim operations
     */
    public ClaimCommand(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    /**
     * Handles the /claim command and all its subcommands.
     *
     * @param sender  the command sender (must be a player)
     * @param command the command object
     * @param label   the command label
     * @param args    command arguments
     * @return true if the command was processed, false otherwise
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (args.length == 0) {
            return handleClaim(player);
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "delete":
                return handleDelete(player);
            case "reload":
                if (!player.hasPermission("claim.admin")) {
                    player.sendRichMessage("<red>You do not have permission to reload claims.</red>");
                    return true;
                }
                claimManager.reloadClaims();
                player.sendRichMessage("<green>All claims reloaded.</green>");
                return true;
            case "trust":
                return handleTrust(player, args);
            case "trustconnected":
                return handleTrustConnected(player, args);
            case "untrust":
                return handleUntrust(player, args);
            case "untrustconnected":
                return handleUntrustConnected(player, args);
            case "showborder":
                return handleBorderShow(player);
            default:
                player.sendRichMessage("<red>Unknown claim command. Use /claim, trust, untrust, reload.</red>");
                return true;
        }
    }

    /**
     * Displays the border of the claim the player is currently standing in.
     *
     * @param player the player viewing the claim border
     * @return true after displaying the border
     */
    private boolean handleBorderShow(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null) {
            player.sendRichMessage("<red>No claim found at this location.</red>");
            return true;
        }
        showClaimBorder(player, chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
        player.sendRichMessage("<green>Showing claim border for 3 seconds.</green>");
        return true;
    }

    /**
     * Attempts to claim the chunk the player is currently standing in.
     *
     * @param player the player making the claim
     * @return true after processing the claim
     */
    private boolean handleClaim(Player player) {
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();

        Claim claim = new Claim(player.getUniqueId(),
                new ArrayList<>(),
                chunk.getX(),
                chunk.getZ(),
                chunk.getWorld().getName());

        if (!claimManager.canPlayerClaim(player.getUniqueId())) {
            player.sendRichMessage("<red>You cannot claim more chunks. Maximum reached.</red>");
            return true;
        }

        if (claimManager.addClaim(claim)) {
            showClaimBorder(player);
            player.sendRichMessage("<green>You have successfully claimed this chunk! " +
                    "<yellow>(X: " + chunk.getX() + ", Z: " + chunk.getZ() + ", World: " + chunk.getWorld().getName() + ")</yellow>");
        } else {
            player.sendRichMessage("<red>This chunk is already claimed.</red>");
        }
        return true;
    }

    /**
     * Deletes the claim the player is currently standing in if they are the owner.
     *
     * @param player the player attempting to delete a claim
     * @return true after processing deletion
     */
    private boolean handleDelete(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null) {
            player.sendRichMessage("<red>No claim found at this location.</red>");
            return true;
        }

        if (!claim.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendRichMessage("<red>You do not own this claim.</red>");
            return true;
        }

        claimManager.removeClaim(claim);
        player.sendRichMessage("<green>Claim deleted successfully.</green>");
        return true;
    }

    /**
     * Adds a trusted player to the claim the player is standing in.
     *
     * @param player the owner of the claim
     * @param args   command arguments, where args[1] is the target player's name
     * @return true after processing
     */
    private boolean handleTrust(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage("<yellow>Usage: /claim trust <player></yellow>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendRichMessage("<red>Player not found.</red>");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null || !claim.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendRichMessage("<red>You do not own this claim.</red>");
            return true;
        }

        claimManager.addTrusted(claim, target.getUniqueId());
        player.sendRichMessage("<green>Trusted " + target.getName() + " on this chunk.</green>");
        return true;
    }

    /**
     * Adds a trusted player to all connected claims adjacent to the current chunk.
     *
     * @param player the owner of the claims
     * @param args   command arguments, where args[1] is the target player's name
     * @return true after processing
     */
    private boolean handleTrustConnected(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage("<yellow>Usage: /claim trustconnected <player></yellow>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendRichMessage("<red>Player not found.</red>");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null || !claim.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendRichMessage("<red>You do not own this claim.</red>");
            return true;
        }

        claimManager.trustConnectedChunks(player.getUniqueId(), target.getUniqueId(),
                chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
        player.sendRichMessage("<green>Trusted " + target.getName() + " on all connected chunks.</green>");
        return true;
    }

    /**
     * Removes a trusted player from the claim the player is standing in.
     *
     * @param player the owner of the claim
     * @param args   command arguments, where args[1] is the target player's name
     * @return true after processing
     */
    private boolean handleUntrust(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage("<yellow>Usage: /claim untrust <player></yellow>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendRichMessage("<red>Player not found.</red>");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null || !claim.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendRichMessage("<red>You do not own this claim.</red>");
            return true;
        }

        claimManager.removeTrusted(claim, target.getUniqueId());
        player.sendRichMessage("<green>Removed trust for " + target.getName() + " on this chunk.</green>");
        return true;
    }

    /**
     * Removes a trusted player from all connected claims adjacent to the current chunk.
     *
     * @param player the owner of the claims
     * @param args   command arguments, where args[1] is the target player's name
     * @return true after processing
     */
    private boolean handleUntrustConnected(Player player, String[] args) {
        if (args.length < 2) {
            player.sendRichMessage("<yellow>Usage: /claim untrustconnected <player></yellow>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendRichMessage("<red>Player not found.</red>");
            return true;
        }

        Chunk chunk = player.getLocation().getChunk();
        Claim claim = claimManager.getClaim(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        if (claim == null || !claim.getOwnerUUID().equals(player.getUniqueId())) {
            player.sendRichMessage("<red>You do not own this claim.</red>");
            return true;
        }

        claimManager.untrustConnectedChunks(player.getUniqueId(), target.getUniqueId(),
                chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
        player.sendRichMessage("<green>Removed trust for " + target.getName() + " on all connected chunks.</green>");
        return true;
    }

    /**
     * Shows the claim border for the chunk the player is currently standing in.
     *
     * @param player the player to show the border to
     */
    private void showClaimBorder(Player player) {
        Location location = player.getLocation();
        showClaimBorder(player, location.getChunk().getX(), location.getChunk().getZ(), location.getWorld().getName());
    }

    /**
     * Shows the claim border for a specific chunk.
     *
     * @param player    the player to show the border to
     * @param startingX the X-coordinate of the chunk
     * @param startingZ the Z-coordinate of the chunk
     * @param worldName the world name of the chunk
     */
    private void showClaimBorder(Player player, long startingX, long startingZ, String worldName) {
        new ClaimParticles(claimManager, YellowMCCoreV2.getInstance())
                .showClaimBorder(player, startingX, startingZ, worldName);
    }

    /**
     * Provides tab completion for the /claim command and its subcommands.
     *
     * @param sender  the command sender
     * @param command the command
     * @param alias   the alias used
     * @param args    the arguments provided
     * @return a list of possible completions for the current argument
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("trust", "trustconnected", "untrust", "untrustconnected", "delete", "reload", "showborder"));
            if (!(sender instanceof Player player) || !player.hasPermission("claim.admin")) {
                options.removeIf(adminCommands::contains);
            }
            return options.stream()
                    .filter(opt -> opt.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2 && List.of("trust", "trustconnected", "untrust", "untrustconnected").contains(args[0].toLowerCase())) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
