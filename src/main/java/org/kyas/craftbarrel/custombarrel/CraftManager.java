package org.kyas.craftbarrel.custombarrel;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CraftManager {
    private List<CustomCraft> crafts = new ArrayList<>();
    private File file;

    public CraftManager(File dataFolder) {
        this.file = new File(dataFolder, "crafts.yml");
        loadCrafts();
    }

    public boolean addCraft(CustomCraft craft) {
        crafts.add(craft);
        return saveCrafts();
    }
    public void removeCraft(CustomCraft craft) {
        crafts.remove(craft);

        deleteCraft(craft);
    }

    private boolean deleteCraft(CustomCraft craft) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("crafts." + craft.getName(), null);

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean saveCrafts() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (int i = 0; i < crafts.size(); i++) {
            CustomCraft craft = crafts.get(i);
            String name = craft.getName();
            config.set("crafts." + name + ".ingredients", craft.getIngredients());
            config.set("crafts." + name + ".result", craft.getResult());
            config.set("crafts." + name + ".duration", craft.getDuration());
        }

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCrafts() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.contains("crafts")) {
            for (String key : config.getConfigurationSection("crafts").getKeys(false)) {
                List<ItemStack> ingredients = (List<ItemStack>) config.getList("crafts." + key + ".ingredients");
                ItemStack result = config.getItemStack("crafts." + key + ".result");
                int duration = config.getInt("crafts." + key + ".duration");

                crafts.add(new CustomCraft(key, ingredients, result, duration));
            }
        }
    }

    public CustomCraft findCraftByIngredients(List<ItemStack> ingredients) {
        for (CustomCraft craft : crafts) {
            List<ItemStack> craftIngredients = new ArrayList<>(craft.getIngredients());
            boolean matches = true;

            for (ItemStack ingredient : ingredients) {
                int index = craftIngredients.indexOf(ingredient);
                if (index == -1) {
                    matches = false;
                    break;
                } else {
                    craftIngredients.remove(index);
                }
            }

            if (matches && craftIngredients.isEmpty()) {
                return craft;
            }
        }
        return null;
    }

    public List<CustomCraft> getCrafts() {
        return crafts;
    }
}
