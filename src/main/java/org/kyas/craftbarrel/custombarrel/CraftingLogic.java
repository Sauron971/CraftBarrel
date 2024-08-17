package org.kyas.craftbarrel.custombarrel;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.kyas.craftbarrel.CraftBarrel;

import java.util.Arrays;
import java.util.List;

public class CraftingLogic implements Listener {

    private final CraftManager craftManager;
    private final CraftBarrel plugin;

    public CraftingLogic(CraftManager craftManager, CraftBarrel plugin) {
        this.craftManager = craftManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void startCrafting(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof InterfaceBarrel) {
            Inventory inv = e.getInventory();
            List<ItemStack> ingredients = Arrays.asList(inv.getContents());
            CustomCraft craft = craftManager.findCraftByIngredients(ingredients);
            if (craft != null) {
                final Block[] block = new Block[1];
                plugin.mapHolders.keySet().forEach((loc) -> {
                    if (plugin.mapHolders.get(loc).equals(inv)) {
                        block[0] = loc.getBlock();
                        block[0].setMetadata("craftBarrel.craft", new FixedMetadataValue(plugin, true));
                    }
                });
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    inv.clear();
                    inv.addItem(craft.getResult());
                    block[0].removeMetadata("craftBarrel.craft", plugin);

                }, craft.getDuration() * 20);

            } else {
                e.getPlayer().sendMessage("Пу-пу-пуу");
            }
        }
    }
}
