package de.emn4tor.utils;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Base64;

public final class ItemStackSerializer {

    public static String serializeItem(@NonNull ItemStack item) {
        var byteArray = item.serializeAsBytes();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static @NonNull ItemStack deserializeItem(String data) {
        var byteArray = Base64.getDecoder().decode(data);
        return ItemStack.deserializeBytes(byteArray);
    }

    public static String serializeItems(ItemStack[] items) {
        var byteArray = ItemStack.serializeItemsAsBytes(items);
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public static ItemStack @NonNull [] deserializeItems(String data) {
        var byteArray = Base64.getDecoder().decode(data);
        return ItemStack.deserializeItemsFromBytes(byteArray);
    }
}
