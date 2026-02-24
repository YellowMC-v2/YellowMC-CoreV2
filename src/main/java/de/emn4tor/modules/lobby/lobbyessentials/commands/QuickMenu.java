package de.emn4tor.modules.lobby.lobbyessentials.commands;

import de.emn4tor.modules.lobby.lobbyessentials.LobbyGUIHolder;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class QuickMenu implements CommandExecutor, Listener {

    private enum MenuButton {
        RTP("<green>RTP", Material.STICK, List.of(
                "<gray>Teleportiere dich zu einem</gray>",
                "<gray>zufälligen Ort auf der Welt</gray>"
        ), new int[]{0, 1, 2, 9, 10, 11}),

        BACK("<yellow>Back to logout position", Material.STICK, List.of(
                "<gray>Teleportiere dich zu deiner</gray>",
                "<gray>letzten Logout Position</gray>"
        ), new int[]{3, 4, 5, 12, 13, 14}),

        HOMES("<red>Homes", Material.STICK, List.of(
                "<gray>Teleportiere dich zu einem deiner Homes</gray>"
        ), new int[]{6, 7, 8, 15, 16, 17}),

        FURNITURE_SHOP("<blue>Möbel Shop", Material.STICK, List.of(
                "<gray>Teleportiere dich zum Möbel Shop</gray>"
        ), new int[]{27, 28, 29, 36, 37, 38}),

        CASINO("<light_purple>Casino", Material.STICK, List.of(
                "<gray>Teleportiere dich zum Casino</gray>"
        ), new int[]{30, 31, 32, 39, 40, 41}),

        CRATES("<gold>Crates", Material.STICK, List.of(
                "<gray>Teleportiere dich zu den Crates</gray>"
        ), new int[]{33, 34, 35, 42, 43, 44}),

        REPORT_BUG("<red>Fehler melden", Material.STICK, List.of(
                "<gray>Berichte einen Fehler im Spiel</gray>"
        ), new int[]{45}),

        CHANGE_LANGUAGE("<yellow>Sprache ändern", Material.STICK, List.of(
                "<gray>Ändere die Sprache des Spiels</gray>"
        ), new int[]{46}),

        DISCORD("<blue>Discord beitreten", Material.STICK, List.of(
                "<gray>Tritt unserem Discord Server bei</gray>"
        ), new int[]{47}),

        CLOSE_MENU("<red>Schließen", Material.STICK, List.of(
                "<gray>Schließe das Menü</gray>"
        ), new int[]{49});


        final String displayName;
        final Material material;
        final List<String> loreLines;
        final int[] slots;

        MenuButton(String displayName, Material material, List<String> loreLines, int[] slots) {
            this.displayName = displayName;
            this.material = material;
            this.loreLines = loreLines;
            this.slots = slots;
        }

        ItemStack toItem() {
            MiniMessage mm = MiniMessage.miniMessage();
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(101);
            meta.displayName(mm.deserialize(displayName));
            meta.lore(loreLines.stream().map(mm::deserialize).toList());
            item.setItemMeta(meta);
            return item;
        }
    }

    private final Map<Integer, MenuButton> slotMap = buildSlotMap();

    private Map<Integer, MenuButton> buildSlotMap() {
        return Map.ofEntries(
                Map.entry(0, MenuButton.RTP), Map.entry(1, MenuButton.RTP), Map.entry(2, MenuButton.RTP),
                Map.entry(9, MenuButton.RTP), Map.entry(10, MenuButton.RTP), Map.entry(11, MenuButton.RTP),

                Map.entry(3, MenuButton.BACK), Map.entry(4, MenuButton.BACK), Map.entry(5, MenuButton.BACK),
                Map.entry(12, MenuButton.BACK), Map.entry(13, MenuButton.BACK), Map.entry(14, MenuButton.BACK),

                Map.entry(6, MenuButton.HOMES), Map.entry(7, MenuButton.HOMES), Map.entry(8, MenuButton.HOMES),
                Map.entry(15, MenuButton.HOMES), Map.entry(16, MenuButton.HOMES), Map.entry(17, MenuButton.HOMES),

                Map.entry(27, MenuButton.FURNITURE_SHOP), Map.entry(28, MenuButton.FURNITURE_SHOP), Map.entry(29, MenuButton.FURNITURE_SHOP),
                Map.entry(36, MenuButton.FURNITURE_SHOP), Map.entry(37, MenuButton.FURNITURE_SHOP), Map.entry(38, MenuButton.FURNITURE_SHOP),

                Map.entry(30, MenuButton.CASINO), Map.entry(31, MenuButton.CASINO), Map.entry(32, MenuButton.CASINO),
                Map.entry(39, MenuButton.CASINO), Map.entry(40, MenuButton.CASINO), Map.entry(41, MenuButton.CASINO),

                Map.entry(33, MenuButton.CRATES), Map.entry(34, MenuButton.CRATES), Map.entry(35, MenuButton.CRATES),
                Map.entry(42, MenuButton.CRATES), Map.entry(43, MenuButton.CRATES), Map.entry(44, MenuButton.CRATES),

                Map.entry(45, MenuButton.REPORT_BUG),
                Map.entry(46, MenuButton.CHANGE_LANGUAGE),
                Map.entry(47, MenuButton.DISCORD),
                Map.entry(49, MenuButton.CLOSE_MENU)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl benutzen.");
            return true;
        }

        Inventory gui = buildGUI();
        player.openInventory(gui);
        return true;
    }

    private Inventory buildGUI() {
        MiniMessage mm = MiniMessage.miniMessage();
        Inventory inv = Bukkit.createInventory(new LobbyGUIHolder(), 54, mm.deserialize("<shift:-10><glyph:quickmenu_gui>"));
        for (MenuButton button : MenuButton.values()) {
            ItemStack item = button.toItem();
            for (int slot : button.slots) inv.setItem(slot, item);
        }
        return inv;
    }

    public MenuButton getButtonBySlot(int slot) {
        return slotMap.get(slot);
    }

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LobbyGUIHolder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        MenuButton button = getButtonBySlot(slot);
        if (button == null) return;

        switch (button) {
            case RTP -> {
                player.closeInventory();
                player.performCommand("rtp");
            }
            case BACK -> {
                player.closeInventory();
                player.performCommand("back");
            }
            case HOMES -> {
                player.closeInventory();
                player.performCommand("homes");
            }
            case FURNITURE_SHOP -> {
                player.closeInventory();
                player.teleport(new Location(player.getWorld(), 75.04, 63.00, -118.53, 90, 0));
            }
            case CRATES -> {
                player.closeInventory();
                player.teleport(new Location(player.getWorld(), -119, -73, 61, 90, 0));
            }
            case CASINO -> {
                player.closeInventory();
                player.sendRichMessage("<red>Das Casino öffnet bald! Bleib dran für Updates.");
            }

            case REPORT_BUG -> {
                player.closeInventory();
                player.performCommand("bug");
            }

            case CHANGE_LANGUAGE -> {
                player.closeInventory();
                player.performCommand("language");
            }

            case DISCORD -> {
                player.closeInventory();
                player.performCommand("discord");
            }

            case CLOSE_MENU -> player.closeInventory();

        }
    }
}
