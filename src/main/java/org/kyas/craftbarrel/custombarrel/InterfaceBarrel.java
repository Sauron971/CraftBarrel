package org.kyas.craftbarrel.custombarrel;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kyas.craftbarrel.CraftBarrel;

import java.util.Arrays;
import java.util.Objects;

public class InterfaceBarrel implements InventoryHolder, Listener {

    private final Inventory inventory;
    private final CraftBarrel plugin;
    private final ItemMeta metaOfBarrelItem;

    public InterfaceBarrel(CraftBarrel plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, InventoryType.HOPPER, "Крафтовая бочка");

        metaOfBarrelItem = new ItemStack(Material.BARREL).getItemMeta();
        assert metaOfBarrelItem != null;
        metaOfBarrelItem.setDisplayName("§rКрафтовая бочка");
        metaOfBarrelItem.setUnbreakable(true);
        metaOfBarrelItem.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }

    @EventHandler
    public void onPlaceBarrel(BlockPlaceEvent e){
        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
            ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
            if (itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE) && itemMeta.isUnbreakable() && itemMeta.hasDisplayName()) {
                if (itemMeta.getDisplayName().equals("Крафтовая бочка")) {
                    e.getBlockPlaced().setMetadata("craftBarrel", new FixedMetadataValue(plugin, true));
                    Inventory inv = Bukkit.createInventory(this, InventoryType.HOPPER, "Крафтовая бочка");
                    plugin.mapHolders.put(e.getBlockPlaced().getLocation(), inv);
                }
            }
        }
    }

    @EventHandler
    public void onInteractBarrel(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getClickedBlock().hasMetadata("craftBarrel") && !e.getClickedBlock().hasMetadata("craftBarrel.craft")) {
                e.setCancelled(true);
                if (plugin.mapHolders.containsKey(e.getClickedBlock().getLocation())) {
                    Inventory inv = plugin.mapHolders.get(e.getClickedBlock().getLocation());
                    e.getPlayer().openInventory(inv);
                }
            } else if (e.getClickedBlock().hasMetadata("craftBarrel.craft")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("Похоже оно еще не готово.");
            }
        }
    }
    @EventHandler
    public void onBreakBarrel(BlockBreakEvent e) {
        Block breakingBlock = e.getBlock();
        if (breakingBlock.hasMetadata("craftBarrel") || breakingBlock.hasMetadata("craftBarrel.craft")) {
            Location loc = breakingBlock.getLocation();
            if (plugin.mapHolders.containsKey(loc)) {
                if (!plugin.mapHolders.get(loc).isEmpty()) {
                    ItemStack[] items = plugin.mapHolders.get(loc).getContents();
                    Arrays.stream(items)
                            .filter(Objects::nonNull)
                            .forEach(item -> breakingBlock.getWorld().dropItemNaturally(loc, item));
                }
                if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    ItemStack barrel = new ItemStack(Material.BARREL);
                    barrel.setItemMeta(metaOfBarrelItem);
                    breakingBlock.setType(Material.AIR);
                    breakingBlock.getWorld().dropItemNaturally(loc, barrel);
                }
                plugin.mapHolders.remove(loc);
                breakingBlock.removeMetadata("craftBarrel", plugin);
                breakingBlock.removeMetadata("craftBarrel.craft", plugin);
            }
        }
    }

    @Override
    @NonNull
    public Inventory getInventory() {
        return inventory;
    }
}
