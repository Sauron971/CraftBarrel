package org.kyas.craftbarrel.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kyas.craftbarrel.custombarrel.CraftManager;
import org.kyas.craftbarrel.custombarrel.CustomCraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ListOfCraftsMenu implements InventoryHolder, GuiKyas {
    private final List<Inventory> inventorySheets = new ArrayList<>();
    private final CraftManager craftManager;
    private final HashMap<ItemStack, CustomCraft> buttons = new HashMap<>();
    private int currentPage = 0;
    private double lastDifferentSize;

    public ListOfCraftsMenu(CraftManager craftManager) {
        this.craftManager = craftManager;

    }

    @Override
    public void fillMenu() {
        if (craftManager.getCrafts().size()/27.0 != lastDifferentSize) {
            for (int i = 0; i < craftManager.getCrafts().size()/27.0; i++) {
                inventorySheets.add(Bukkit.createInventory(this, 36, "Лист всех крафтов"));
            }
            lastDifferentSize = craftManager.getCrafts().size()/27.0;
        }
        List<CustomCraft> crafts = new ArrayList<>(craftManager.getCrafts());
        for (Inventory inv : inventorySheets) {
            for (int i = 0; i < 27; i++) {
                if (i >= crafts.size()) {
                    break;
                }
                CustomCraft craft = crafts.get(i);
                ItemStack temp = setNameLore(craft.getResult(), craft.getName(), Arrays.asList("§fВремя: " + craft.getDuration()));
                addNewButton(inv, craftManager.getCrafts().get(i), temp);
            }
            buttons.forEach((itemStack, craft) -> crafts.remove(craft));

            ItemStack temp = setNameLore(new ItemStack(Material.RED_STAINED_GLASS_PANE), "Вернуться назад", null);
            setNewButton(inv, null, temp, 27);
            temp = setNameLore(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "Перелистнуть", null);
            setNewButton(inv, null, temp, 35);

        }
    }

    @Override
    public void openMenu(Player p) {
        clearPages();
        fillMenu();
        if (!inventorySheets.isEmpty()) {
            p.sendMessage(String.valueOf(inventorySheets.size()));
            p.openInventory(inventorySheets.get(0));
        }
    }
    public void openNextPage(Player p) {
        if (currentPage+1 < inventorySheets.size()) {
            p.openInventory(inventorySheets.get(++currentPage));
        }
    }
    public void openPreviousPage(Player p) {
        if (currentPage-1 >= 0) {
            p.openInventory(inventorySheets.get(--currentPage));
        }
    }
    private void clearPages() {
        for (Inventory inv :
                inventorySheets) {
            inv.clear();
        }
    }

    private void addNewButton(Inventory inv, CustomCraft craft, ItemStack item) {
        buttons.put(item, craft);
        inv.addItem(item);
    }

    private void setNewButton(Inventory inv, CustomCraft craft, ItemStack item, int index) {
        buttons.put(item, craft);
        inv.setItem(index, item);
    }

    private ItemStack setNameLore(ItemStack item, String displayName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§6" + displayName);
        if (lore != null) {
            meta.setLore(lore);
        }
        ItemStack newItem = new ItemStack(item.getType());
        newItem.setItemMeta(meta);
        return newItem;
    }

    public HashMap<ItemStack, CustomCraft> getButtons() {
        return buttons;
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inventorySheets.get(0);
    }
}
