package de.emn4tor.modules.lobby.crates.model;

/**
 * Enum representing the different types of rewards that can be given from a crate.
 * <p>
 * This enum is used to identify the type of reward when processing crate openings
 * and determining how to apply the reward to the player.
 */

public enum RewardType {
    MONEY,
    RUBIES,
    ITEM,
    NEXO_ITEM,
    PERMISSION,
    COMMAND,
    CRATE_KEY
}
