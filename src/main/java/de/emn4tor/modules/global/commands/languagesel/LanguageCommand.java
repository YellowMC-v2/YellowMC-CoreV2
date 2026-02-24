package de.emn4tor.modules.global.commands.languagesel;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class LanguageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        openLangGUI(player, 0);
        return true;
    }

    public static List<InvModel> languages = List.of(
            new InvModel(0, Locale.ENGLISH, ""),
            new InvModel(1, Locale.GERMAN, "")
    );

    public static void openLangGUI(Player player, int page) {
        // Ensure page is within bounds
        if (page < 0 || page >= languages.size()) {
            page = 0;
        }

        InvModel currentLang = languages.get(page);
        String titleGlyph = currentLang.getLocale().equals(Locale.ENGLISH) ? "glyph:language_en" : "glyph:language_de";

        Inventory inventory = Bukkit.createInventory(
                new LanguageInvHolder(page),
                54,
                MiniMessage.miniMessage().deserialize("<shift:-10><" + titleGlyph + ">")
        );

        // Left arrows (slots 2 and 3)
        ItemStack leftArrow = createArrow(Material.STICK, currentLang.getLocale().equals(Locale.ENGLISH) ? "Previous" : "Zurück");
        inventory.setItem(1, leftArrow);
        inventory.setItem(2, leftArrow);

        // Right arrows (slots 6 and 7)
        ItemStack rightArrow = createArrow(Material.STICK, currentLang.getLocale().equals(Locale.ENGLISH) ? "Next" : "Weiter");
        inventory.setItem(6, rightArrow);
        inventory.setItem(7, rightArrow);

        // Apply/Fertig buttons (slots 48, 49, 50)
        String applyText = currentLang.getLocale().equals(Locale.ENGLISH) ? "APPLY" : "FERTIG";
        ItemStack applyButton = createApplyButton(applyText);
        inventory.setItem(39, applyButton);
        inventory.setItem(40, applyButton);
        inventory.setItem(41, applyButton);

        // Add language display item in center
        ItemStack langItem = createLanguageItem(currentLang);
        inventory.setItem(22, langItem);

        player.openInventory(inventory);
    }

    private static ItemStack createArrow(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(101);
            meta.displayName(MiniMessage.miniMessage().deserialize("<white>" + name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createApplyButton(String text) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(101);
            meta.displayName(MiniMessage.miniMessage().deserialize("<green><bold>" + text));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createLanguageItem(InvModel lang) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(101);
            String displayName = lang.getLocale().equals(Locale.ENGLISH) ? "English" : "Deutsch";
            meta.displayName(MiniMessage.miniMessage().deserialize("<yellow><bold>" + displayName));
            item.setItemMeta(meta);
        }
        return item;
    }
}