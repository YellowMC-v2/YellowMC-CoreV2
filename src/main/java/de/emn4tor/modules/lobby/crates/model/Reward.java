package de.emn4tor.modules.lobby.crates.model;

public class Reward {
    private final RewardType type;
    private final String displayName;
    private final double weight; // Chance weight for this reward
    private final String command; // For COMMAND type, the command to execute
    private final int amount; // For MONEY and RUBIES types, the amount to give
    private final String permission; // For PERMISSION type, the permission to grant
    private final String itemId; // For ITEM and NEXO_ITEM types, the ID of the item to give
    private final int itemAmount; // For ITEM and NEXO_ITEM types, the amount of the item to give
}
