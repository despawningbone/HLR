package me.despawningbone.HLR;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HLRSubCommand implements CommandExecutor {
	private HLRmain plugin;
	
	public HLRSubCommand(HLRmain instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			ConfigHandler configHandler = plugin.configHandler;
			if (args.length <= 0){
				sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.UnknownArgument"));
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
					if(sender.hasPermission("HLR.reload")){
						configHandler.reloadConfig(sender, ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.ReloadedPlugin"));	
					} else {
						sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.NoPermsReload"));
					}
				} else if (args[0].equalsIgnoreCase("give")) {
					if(sender.hasPermission("HLR.give")) {
						if(args.length < 1) {
							sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.NoRecipient"));
						} else {
							Player player = Bukkit.getServer().getPlayer(args[1]);
							if(player != null) {
								ItemStack item = new ItemStack(Material.HOPPER);
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(HLRmain.CHname);
								meta.setLore(HLRmain.hopperlore);
								item.setItemMeta(meta);
								int amount = 1;
								if(args.length > 2) {
									try {
										amount = Integer.parseInt(args[2]);
									} catch (NumberFormatException e) {
										sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.InvalidAmount"));
										return true;
									}
								}
								item.setAmount(amount);
								player.getInventory().addItem(item);
								if(sender.getName().equals(player.getName())) {
									sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.GiveToSelf")
											.replaceAll("%player%", sender.getName())
											.replaceAll("%amount%", String.valueOf(amount)));
								} else {
									String get = sender instanceof ConsoleCommandSender ? "SubCmds.GiveCmd.ReceiveHopperFromConsole" : "SubCmds.GiveCmd.ReceiveHopper";
									player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get(get)
											.replaceAll("%player%", sender.getName())
											.replaceAll("%amount%", String.valueOf(amount)));
									sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.GiveHopper")
											.replaceAll("%player%", player.getName())
											.replaceAll("%amount%", String.valueOf(amount)));	
								}
							} else {
								sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.PlayerNotFound"));
							}
						}
					} else {
						sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.GiveCmd.NoPermsGive"));
					}
				} else if (args[0].equalsIgnoreCase("about")) {
					sender.sendMessage(ChatColor.DARK_GRAY + "HLR" + ChatColor.GRAY + " Version: " + ChatColor.GREEN + plugin.getDescription().getVersion());
					sender.sendMessage(ChatColor.GOLD + "Made by " + ChatColor.DARK_BLUE + "despawningbone");
				} else {
					sender.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("SubCmds.UnknownArgument"));
				}
			}
		return true;
	}
}
