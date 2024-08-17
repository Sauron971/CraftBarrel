package org.kyas.craftbarrel.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kyas.craftbarrel.CraftBarrel;
import org.kyas.craftbarrel.custombarrel.CraftManager;
import org.kyas.craftbarrel.custombarrel.CustomCraft;

import java.util.ArrayList;
import java.util.List;

public class NewCraftMenuListener implements Listener {
    private NewCraftMenu menu = new NewCraftMenu();
    private final CraftManager craftManager;
    private final CraftBarrel plugin;
    private String nameOfCraft;
    private long durationOfCraft;

    public NewCraftMenuListener(CraftManager craftManager, CraftBarrel plugin) {
        this.craftManager = craftManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickButtons(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory != null) {
            if (inventory.getHolder() instanceof NewCraftMenu) {
                ItemStack clickedItem = e.getCurrentItem();
                if (clickedItem != null) {
                    Player p = (Player) e.getWhoClicked();

                    if (menu.getButtons().get("btn_cancel").equals(clickedItem)) {
                        new MainMenu().openMenu(p);

                    }
                    else if (menu.getButtons().get("btn_save").equals(clickedItem)) {
                        if (durationOfCraft == 0 || nameOfCraft == null) {
                            durationOfCraft = 300;
                            nameOfCraft = "Новый крафт";
                        }
                        CustomCraft newCraft = getCraft(inventory, nameOfCraft, durationOfCraft);
                        boolean b = craftManager.addCraft(newCraft);
                        p.closeInventory();
                        menu = new NewCraftMenu();
                        if (b) {
                            p.sendMessage("Крафт сохранен успешно!");
                        } else {
                            p.sendMessage("Похоже пре сохранение произошла ошибка!");
                        }
                    } else if (menu.getButtons().get("btn_Time").equals(clickedItem)) {
                        startSettingTime(p);


                    } else if (menu.getButtons().get("btn_Name").equals(clickedItem)) {
                        startSettingName(p);

                    }

                    if (menu.getButtons().containsValue(clickedItem)) {
                        e.setCancelled(true);

                    }

                }
            }
        }
    }
    private boolean isSettingTime = false;
    private boolean isSettingName = false;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (isSettingTime) {
            e.setCancelled(true); // Отменяем событие, чтобы сообщение не отображалось в чате
            try {
                durationOfCraft = Long.parseLong(e.getMessage());
                p.sendMessage("Время установлено на: " + durationOfCraft + " секунд");
                isSettingTime = false; // Сброс флага
                menu.setTimeButton((int) durationOfCraft);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> menu.openMenu(p), 30L);

            } catch (NumberFormatException exception) {
                p.sendMessage("Вы ввели не число. Попробуйте снова.");
            }
        } else if (isSettingName) {
            e.setCancelled(true);
            nameOfCraft = e.getMessage();
            p.sendMessage("Имя крафта установлено на: " + nameOfCraft);
            menu.setNameButton(nameOfCraft);
            isSettingName = false; // Сброс флага
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> menu.openMenu(p), 30L);
        }
    }

    // Метод для начала ввода времени
    public void startSettingTime(Player player) {
        player.closeInventory();
        isSettingTime = true;
        player.sendMessage("Введите время в секундах:");
    }

    // Метод для начала ввода имени
    public void startSettingName(Player player) {
        player.closeInventory();
        isSettingName = true;
        player.sendMessage("Введите имя крафта:");
    }

    private CustomCraft getCraft(Inventory inventory, String name, long duration) {
        List<ItemStack> ingredients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ingredients.add(inventory.getItem(10+i));
        }
        return new CustomCraft(name, ingredients, inventory.getItem(16), duration);
    }
}
