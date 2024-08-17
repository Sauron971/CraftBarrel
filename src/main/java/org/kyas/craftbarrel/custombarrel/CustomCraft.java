package org.kyas.craftbarrel.custombarrel;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class CustomCraft {
    private String name;
    private List<ItemStack> ingredients;
    private ItemStack result;
    private long duration;

    public CustomCraft(String name, List<ItemStack> ingredients, ItemStack result, long duration) {
        this.name = name;
        this.ingredients = ingredients;
        this.result = result;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomCraft craft = (CustomCraft) o;
        return duration == craft.duration && Objects.equals(name, craft.name) && Objects.equals(ingredients, craft.ingredients) && Objects.equals(result, craft.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, result, duration);
    }
}
