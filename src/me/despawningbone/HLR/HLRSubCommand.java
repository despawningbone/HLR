package me.despawningbone.HLR;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HLRSubCommand implements CommandExecutor {
	private HLRmain plugin;
	
	public HLRSubCommand(HLRmain instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			ConfigHandler configHandler = new ConfigHandler(plugin);
			if (args.length <= 0){
				sender.sendMessage(ChatColor.RED + "Unknown argument. Please type /HLR help for the help menu.");
				return true;
			} else {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.GRAY + "-------" + ChatColor.DARK_AQUA + "HLR Help" + ChatColor.GRAY + "-------");
					sender.sendMessage(ChatColor.RED + "/converthopper" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "The main command of this plugin.");
					sender.sendMessage(ChatColor.YELLOW + "Converts a normal hopper into a " + ChatColor.GREEN + ChatColor.stripColor(HLRmain.CHname) + ChatColor.YELLOW  + ".");
					sender.sendMessage(ChatColor.GRAY + "Alias:" + ChatColor.YELLOW + " /chopper");
					sender.sendMessage(ChatColor.RED + "/HLR help" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Displays this page.");
					if(sender.hasPermission("HLR.reload")) {
						sender.sendMessage(ChatColor.RED + "/HLR reload" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Reloads the config.");
					}
					sender.sendMessage(ChatColor.RED + "/HLR about" + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Displays the about page.");
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("HLR.reload")){
						configHandler.reloadConfig(sender, ChatColor.BLUE + "HLR has been reloaded.");	
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have the permission to use this command.");
					}
				} else if (args[0].equalsIgnoreCase("about")) {
					sender.sendMessage(ChatColor.DARK_GRAY + "HLR" + ChatColor.GRAY + " Version: " + ChatColor.GREEN + "1.0.0");
					sender.sendMessage(ChatColor.GOLD + "Made by " + ChatColor.DARK_BLUE + "despawningbone");
				} else {
					sender.sendMessage(ChatColor.RED + "Unknown argument. Please type /HLR help for the help menu.");
				}
			}
		return true;
	}
}
