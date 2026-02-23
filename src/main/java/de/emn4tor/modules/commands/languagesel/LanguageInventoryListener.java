package de.emn4tor.modules.commands.languagesel;

import de.emn4tor.YellowMCCoreV2;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class LanguageInventoryListener implements Listener {

    public static final Set<UUID> switchingPage = new HashSet<>();

    private final LanguageCommand languageCommand;

    public LanguageInventoryListener(LanguageCommand languageCommand) {
        this.languageCommand = languageCommand;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LanguageInvHolder)) {
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        LanguageInvHolder holder = (LanguageInvHolder) event.getInventory().getHolder();
        int currentPage = holder.getCurrentPage();
        int slot = event.getSlot();

        // Left arrows (previous)
        if (slot == 2 || slot == 3) {
            int newPage = currentPage - 1;
            if (newPage < 0) {
                newPage = languageCommand.languages.size() - 1;
            }
            switchingPage.add(player.getUniqueId());
            languageCommand.openLangGUI(player, newPage);
        }
        // Right arrows (next)
        else if (slot == 6 || slot == 7) {
            int newPage = (currentPage + 1) % languageCommand.languages.size();
            switchingPage.add(player.getUniqueId());
            languageCommand.openLangGUI(player, newPage);
        }
        // Apply buttons
        else if (slot == 39 || slot == 40 || slot == 41) {
            Locale selectedLocale = languageCommand.languages.get(currentPage).getLocale();
            YellowMCCoreV2.getLocaleService().setLocale(player.getUniqueId(), selectedLocale);
            player.closeInventory();
            player.sendMessage("Language switched to: " + selectedLocale.getDisplayLanguage()); //TODO: Use translator here
        }
    }


}