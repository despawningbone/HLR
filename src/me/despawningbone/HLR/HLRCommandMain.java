package me.despawningbone.HLR;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HLRCommandMain implements CommandExecutor {
	
	private HLRmain plugin;
	
	//public static Logger log;   //debug
	
	
	public HLRCommandMain(HLRmain instance) {
		plugin = instance;
	}
	
	public static boolean confirm = false;
	public static HashMap<Player, Boolean> start = new HashMap<Player, Boolean>();
	public ConfigHandler configHandler;
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
			boolean canUseCommand = true;
			boolean paying = false;
			boolean convert = true;
			configHandler = new ConfigHandler(plugin);
			//String sstart = String.valueOf(start);   //debug
			//sender.sendMessage(sstart);     //debug
			if (sender instanceof Player) {
			Player player = (Player) sender;
			double fee = configHandler.fee;
			boolean useEco = configHandler.useEco;

			double money = 0;
			if (useEco){
				money = HLRmain.getMoney(player);
				//player.sendMessage(String.valueOf(money));   //debug
				//player.sendMessage(String.valueOf(fee));   //debug
			}
			if (configHandler.usePerms) {
					canUseCommand = player.hasPermission("HLR.convert");
			}
			if (!canUseCommand)
				sender.sendMessage(ChatColor.RED + "You don't have permissions to do that.");

			if (canUseCommand && fee > 0 && useEco) {
				paying = true;
				if (money < fee) {
					canUseCommand = false;
					player.sendMessage(ChatColor.RED + "You don't have enough money to convert the hopper.");
				}

				if(configHandler.usePerms){
					if(player.hasPermission("HLR.nofee")){
						//player.sendMessage("no need to pay");   //debug
						paying = false;		
					}
				} else if(player.isOp()) {
					paying = false;
				}
			}
			if (canUseCommand) {
				ItemStack item; ItemMeta meta;
				if(Integer.parseInt(HLRmain.ver.split("\\.")[1].trim()) >= 9) {
					item = player.getInventory().getItemInMainHand();
					meta = player.getInventory().getItemInMainHand().getItemMeta();	
				} else {
					item = player.getItemInHand();
					meta = player.getItemInHand().getItemMeta();
				}
				String CHname = HLRmain.CHname;
				int maxamount = configHandler.maxamount;
				if(configHandler.usePerms) {
					if(player.hasPermission("HLR.limitbypass")) {
						maxamount = 64;	
					}
				} else if(player.isOp()) {
					maxamount = 64;
				}
				if(start.isEmpty() || !start.containsKey(player)) {
					start.put(player, false);
				}
				if(!configHandler.cooldown || !start.get(player)) {
					if(item.getType().equals(Material.HOPPER)){
						if(item.getAmount() <= maxamount) {
							if (meta.hasLore() && meta.hasDisplayName()){
								if(meta.getLore().equals(HLRmain.hopperlore) || meta.getDisplayName().equals(HLRmain.CHname)){
									convert = false;
								}
							}
							if(convert) {
								meta.setDisplayName(CHname);
								meta.setLore(HLRmain.hopperlore);
								item.setItemMeta(meta);
								player.sendMessage(ChatColor.GREEN + "Successfully converted the hopper to a " + ChatColor.YELLOW + ChatColor.stripColor(HLRmain.CHname) + ChatColor.GREEN + "!");
								if(configHandler.cooldown) {
									if(configHandler.usePerms) {
										if(!player.hasPermission("HLR.nocooldown")) {
											//player.sendMessage("Start-nperms");  //debug
											start.put(player, true);
											Timer timer = new Timer();
											timer.main(player);			
										}
									} else if(!player.isOp()) {
										//player.sendMessage("Start-nop");  //debug
										start.put(player, true);
										Timer timer = new Timer();
										timer.main(player);
									}
								}
								if (paying) {
										HLRmain.econ.withdrawPlayer(player, fee);
										paying = false;
										player.sendMessage(ChatColor.BLUE + "This transaction cost you " + ChatColor.GOLD + "$" + ChatColor.GREEN + fee + ChatColor.BLUE + ".");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You have already converted the hopper.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You cannot convert more than " + ChatColor.YELLOW + configHandler.maxamount + ChatColor.RED + " hoppers at once.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are not holding a hopper right now.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "This command is still cooling down.");
				}
			} 
		} else {
			sender.sendMessage(ChatColor.RED + "This is a player only command.");
		}
		return true;
	}
}
