package org.kyas.craftbarrel.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainMenu implements InventoryHolder, GuiKyas {

    private final Inventory inv = Bukkit.createInventory(this, 27, "Главное меню");
    private final HashMap<String, ItemStack> buttons = new HashMap<>();

    public HashMap<String, ItemStack> getButtons() {
        return buttons;
    }

    public MainMenu() {
        fillMenu();
    }

    @Override
    public void fillMenu() {
        ItemStack temp = setNameLore(new ItemStack(Material.CRAFTING_TABLE), "Создать новый крафт", Arrays.asList("ПКМ для открытия", "меню создания"));
        addNewButton("btn_newCraft", temp);
        temp = setNameLore(new ItemStack(Material.CRAFTING_TABLE), "Посмотреть крафты", Arrays.asList("ПКМ для открытия", "меню созданных крафтов"));
        addNewButton("btn_listCrafts", temp);
    }

    @Override
    public void openMenu(Player p) {
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    private void addNewButton(String nameButton, @NonNull ItemStack item) {
        buttons.put(nameButton, item);
        inv.addItem(item);
    }

    private void setNewButton(String nameButton, ItemStack item, int index) {
        buttons.put(nameButton, item);
        inv.setItem(index, item);
    }

    private ItemStack setNameLore(ItemStack item, String displayName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName("§r" + displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inv;
    }
}
