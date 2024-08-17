package org.kyas.craftbarrel.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class MainMenuListener implements Listener {

    private final MainMenu menu = new MainMenu();
    private final ListOfCraftsMenu listOfCraftsMenu;

    public MainMenuListener(ListOfCraftsMenu listOfCraftsMenu) {
        this.listOfCraftsMenu = listOfCraftsMenu;
    }

    @EventHandler
    public void onClickButtons(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory != null) {
            if (inventory.getHolder() instanceof MainMenu) {
                ItemStack clickedItem = e.getCurrentItem();
                if (clickedItem != null) {
                    Player p = (Player) e.getWhoClicked();
                    if (menu.getButtons().get("btn_newCraft").equals(clickedItem)) {
                        new NewCraftMenu().openMenu(p);
                    }
                    else if (menu.getButtons().get("btn_listCrafts").equals(clickedItem)) {
                        listOfCraftsMenu.openMenu(p);
                    }

                    if (menu.getButtons().containsValue(clickedItem)) {
                        e.setCancelled(true);

                    }

                }
            }
        }
    }
}
