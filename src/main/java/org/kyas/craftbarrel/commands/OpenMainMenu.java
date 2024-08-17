package org.kyas.craftbarrel.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kyas.craftbarrel.CraftBarrel;
import org.kyas.craftbarrel.gui.MainMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class OpenMainMenu implements CommandExecutor, TabCompleter {
    private final CraftBarrel plugin;

    public OpenMainMenu(CraftBarrel plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (p.hasPermission("craftBarrel.command.usage")) {
                switch (args[0]) {
                    case "menu":
                        new MainMenu().openMenu(p);
                        return true;
                    case "give":
                        ItemStack barrel = new ItemStack(Material.BARREL);
                        ItemMeta meta = barrel.getItemMeta();
                        meta.setDisplayName("§rКрафтовая бочка");
                        meta.setUnbreakable(true);
                        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                        barrel.setItemMeta(meta);
                        p.getInventory().addItem(barrel);
                        p.sendTitle("", "Бочка выдана успешно", 10, 20, 10);
                        break;
                    case "checkBarrels":
                        Location[] locations = plugin.mapHolders.keySet().toArray(new Location[0]);
                        p.sendMessage("Всего бочек в мире: " + plugin.mapHolders.size());
                        Arrays.stream(locations).forEach(loc -> {
                            double x = loc.getX();
                            double y = loc.getY();
                            double z = loc.getZ();
                            p.sendMessage("Бочка находится на координатах \n" +
                                    "x=" + x + "\n" +
                                    "y=" + y + "\n" +
                                    "z=" + z);
                        });
                        break;
                    case "loadCrafts":
                        plugin.craftManager.loadCrafts();
                        p.sendMessage("Загрузка крафтов прошла успешно!");
                        break;
                }
            }

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            List<String> subCommands = Arrays.asList("menu", "give", "checkBarrels");
            List<String> completions = new ArrayList<>();
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0])) {
                    completions.add(subCommand);
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}
