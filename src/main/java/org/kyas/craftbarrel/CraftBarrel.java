package org.kyas.craftbarrel;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.kyas.craftbarrel.commands.OpenMainMenu;
import org.kyas.craftbarrel.custombarrel.CraftManager;
import org.kyas.craftbarrel.custombarrel.CraftingLogic;
import org.kyas.craftbarrel.custombarrel.InterfaceBarrel;
import org.kyas.craftbarrel.gui.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class CraftBarrel extends JavaPlugin {

    public HashMap<Location, Inventory> mapHolders;
    private final File fileMapHolders = new File(this.getDataFolder() + "/saving/savedBarrels.yml");
    public FileConfiguration saveMapHolders;
    public CraftManager craftManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getServer().getConsoleSender().sendMessage(
                "§6           __  __                                     _      ________ \n" +
                        "§6          /\\ \\/\\ \\                                  /' \\    /\\_____  \\\n" +
                        "§6          \\ \\ \\/'/'    __  __       __       ____  /\\_, \\   \\/___//'/'\n" +
                        "§6           \\ \\ , <    /\\ \\/\\ \\    /'__`\\    /',__\\ \\/_/\\ \\      /' /' \n" +
                        "§6            \\ \\ \\\\`\\  \\ \\ \\_\\ \\  /\\ \\L\\.\\_ /\\__, `\\   \\ \\ \\   /' /'   \n" +
                        "§6             \\ \\_\\ \\_\\ \\/`____ \\ \\ \\__/.\\_\\\\/\\____/    \\ \\_\\ /\\_/     \n" +
                        "§6  _______     \\/_/\\/_/  `/___/> \\ \\/__/\\/_/ \\/___/      \\/_/ \\//      \n" +
                        "§6 /\\______\\                 /\\___/                                     \n" +
                        "§6 \\/______/                 \\/__/                                      \n");



        saveMapHolders = YamlConfiguration.loadConfiguration(fileMapHolders);
        mapHolders = new HashMap<>();
        loadMapHolders();
        craftManager = new CraftManager(new File(getDataFolder() + "/saving"));
        craftManager.getCrafts().forEach(craft -> this.getServer().getConsoleSender().sendMessage("§9[CraftBarrel]Загружен крафт с названием: §e" + craft.getName()));
        //registration
        registerEvents();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveMapHolders();
        craftManager.saveCrafts();
        removeBarrelsWhenCrafting();
    }
    private void registerEvents() {
        ListOfCraftsMenu menuCrafts = new ListOfCraftsMenu(craftManager);

        InterfaceBarrel barrels = new InterfaceBarrel(this);
        getServer().getPluginManager().registerEvents(new MainMenuListener(menuCrafts), this);
        getServer().getPluginManager().registerEvents(new ListOfCraftsMenuListener(menuCrafts, craftManager, this), this);
        getServer().getPluginManager().registerEvents(new CraftingLogic(craftManager, this), this);
        getServer().getPluginManager().registerEvents(barrels, this);
        getServer().getPluginManager().registerEvents(new NewCraftMenuListener(craftManager, this), this);
        Objects.requireNonNull(getServer().getPluginCommand("craftsBarrel")).setExecutor(new OpenMainMenu(this));
    }

    private void removeBarrelsWhenCrafting() {
        mapHolders.keySet().forEach(loc -> {
            Block block = loc.getBlock();
            if (block.hasMetadata("craftBarrel.craft")) {
                block.removeMetadata("craftBarrel.craft", this);
            }
        });
    }

    private void loadMapHolders() {
        if (!fileMapHolders.exists()) {
            createDirectoryAndFile();
            return;
        }
        Object section = saveMapHolders.get("barrel0");
        if (section != null) {
            saveMapHolders.getKeys(false).forEach(key -> {
                List<ItemStack> contents = (List<ItemStack>) saveMapHolders.getList(key + ".inventory");
                Inventory inv = new InterfaceBarrel(this).getInventory();
                inv.setContents(contents.toArray(new ItemStack[contents.size()]));
                Location loc = getLocation(key);
                mapHolders.put(loc, inv);
            });

            mapHolders.forEach((location, itemStacks) -> {
                Block block = location.getBlock();
                block.setMetadata("craftBarrel", new FixedMetadataValue(this, true));
            });
        }
    }

    private void saveMapHolders() {
        if (!fileMapHolders.exists()) {
            createDirectoryAndFile();
        }
        if (mapHolders.isEmpty()) {
            fileMapHolders.delete();
            return;
        }
        FileConfiguration section = saveMapHolders;
        AtomicInteger i = new AtomicInteger();
        mapHolders.forEach((location, inventory) -> {
            String path = "barrel" + i + ".";
            List<ItemStack> inv = Arrays.asList(inventory.getContents());
            saveLocation(i.get(), location);
            section.set(path + "inventory", inv);
            i.getAndIncrement();
        });


        try {
            saveMapHolders.save(fileMapHolders);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void createDirectoryAndFile() {
        File dirs = new File(this.getDataFolder() + "/saving");
        try {
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            fileMapHolders.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveLocation(int index, Location loc) {
        String path = "barrel" + index + ".location.";

        saveMapHolders.set(path + "world", loc.getWorld().getName());
        saveMapHolders.set(path + "x", loc.getX());
        saveMapHolders.set(path + "y", loc.getY());
        saveMapHolders.set(path + "z", loc.getZ());
    }
    private Location getLocation(String key) {
        String world = saveMapHolders.getString(key + ".location.world");
        double x = saveMapHolders.getDouble(key + ".location.x");
        double y = saveMapHolders.getDouble(key + ".location.y");
        double z = saveMapHolders.getDouble(key + ".location.z");
        return new Location(getServer().getWorld(world), x, y, z);
    }
}
