package org.kyas.craftbarrel.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kyas.craftbarrel.custombarrel.CustomCraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NewCraftMenu implements InventoryHolder, GuiKyas {

    private Inventory inv = Bukkit.createInventory(this, 36, "Новый крафт");
    private final HashMap<String, ItemStack> buttons = new HashMap<>();

    public NewCraftMenu() {
        fillMenu();
    }

    @Override
    public void fillMenu() {
        for (int i = 0; i < inv.getSize(); i++) {
            setNewButton("fillingPanel" + i, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), i);
        }
        ItemStack temp = new ItemStack(Material.AIR);
        inv.setItem(10, temp);
        inv.setItem(11, temp);
        inv.setItem(12, temp);
        inv.setItem(13, temp);
        inv.setItem(14, temp);
        inv.setItem(16, temp);
        temp = setNameLore(new ItemStack(Material.RED_STAINED_GLASS_PANE), "Отмена", null);
        setNewButton("btn_cancel", temp, 27);
        temp = setNameLore(new ItemStack(Material.RED_STAINED_GLASS_PANE), "Сохранить", null);
        setNewButton("btn_save", temp, 35);
        temp = setNameLore(new ItemStack(Material.BOOK), "Информация", Arrays.asList("Всего может быть 5 ингредиентов", "Для отмены нажмите слева", "Для сохранения нажмите справа"));
        setNewButton("btn_Info", temp, 32);
        setTimeButton(300);
        setNameButton("Новый крафт");
    }

    public void setTimeButton(int time) {
        ItemStack temp = setNameLore(new ItemStack(Material.CLOCK), "Ввести время", Collections.singletonList("Время крафта: " + time));
        setNewButton("btn_Time", temp, 30);
    }
    public void setNameButton(String name) {
        ItemStack temp = setNameLore(new ItemStack(Material.MAP), "Название крафта: " + name, Collections.singletonList("ПКМ для изменения названия"));
        setNewButton("btn_Name", temp, 31);
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
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void openMenu(Player p) {
        p.openInventory(inv);
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inv;
    }

    public HashMap<String, ItemStack> getButtons() {
        return buttons;
    }

}
