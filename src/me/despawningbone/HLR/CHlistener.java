package me.despawningbone.HLR;

import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class CHlistener implements Listener {
	
	private HLRmain plugin;
	
	public static HashMap<Entry<World, Chunk>, List<Location>> blockInfo = new HashMap<Entry<World, Chunk>, List<Location>>	();
	

	public CHlistener(HLRmain instance) {
		plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = (Player) event.getPlayer();
	    String world = event.getBlock().getWorld().getName();
		if(plugin.isEnabledIn(world)) {
		    ItemStack Mblock; ItemStack Oblock; EquipmentSlot PHand = null; 
		    boolean old = false; boolean inMain = false; boolean inOff = false;
		    //plugin.log.info(HLRmain.ver.split("\\.")[1]);   //debug
		    if(Integer.parseInt(HLRmain.ver.split("\\.")[1].trim()) >= 9) {
		    	//plugin.log.info("dual-wielding");   //debug
				Mblock = player.getInventory().getItemInMainHand();
				Oblock = player.getInventory().getItemInOffHand();
				PHand = event.getHand();
		    } else {
		    	//plugin.log.info("old school");  //debug
		    	Mblock = player.getItemInHand();
		    	Oblock = Mblock;
		    	old = true;
		    }
		    if(Mblock.hasItemMeta()) {
		    	if(Mblock.getItemMeta().hasDisplayName() && Mblock.getItemMeta().hasLore() && Mblock.getType() == Material.HOPPER) {
			    	if(Mblock.getItemMeta().getDisplayName().equals(HLRmain.CHname) && Mblock.getItemMeta().getLore().equals(HLRmain.hopperlore)) {
			    		inMain = true;
			    	}
			    }	
		    }
		    if(Oblock.hasItemMeta()) {
		    	if(Oblock.getItemMeta().hasDisplayName() && Oblock.getItemMeta().hasLore() && Oblock.getType() == Material.HOPPER) {
			    	if(Oblock.getItemMeta().getDisplayName().equals(HLRmain.CHname) && Oblock.getItemMeta().getLore().equals(HLRmain.hopperlore)) {
			    		inOff = true;
			    	}
			    }	
		    }

			if(inMain || inOff) {
				if(old || (inMain && PHand == EquipmentSlot.HAND) || (inOff && PHand == EquipmentSlot.OFF_HAND)) {
					File DataFile = new File(plugin.getDataFolder() + File.separator
							+ "Data.yml");
					YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
				    double x = event.getBlock().getX();
				    double y = event.getBlock().getY();
				    double z = event.getBlock().getZ();
				    //player.sendMessage(String.valueOf(x));   //debug
				    //player.sendMessage(String.valueOf(y));   //debug
				    //player.sendMessage(String.valueOf(z));   //debug
				    String coord = x + "," + y + "," + z;
				    //player.sendMessage(coord);    //debug
				    Location loc = event.getBlock().getLocation();
				    Map.Entry<World, Chunk> entry = new AbstractMap.SimpleEntry<World, Chunk>(event.getBlock().getWorld(), event.getBlock().getLocation().getChunk());
				    if (!CHlistener.blockInfo.containsKey(entry)) {
					    List<Location> list = new ArrayList<Location>();
					    list.add(loc);

					    blockInfo.put(entry, list);
					} else {
					    blockInfo.get(entry).add(loc);
					}	
				    List<String> coordlist = DFile.getStringList(world);
				    coordlist.add(coord);
				    DFile.set(world, coordlist);
				    try {
			            DFile.save(plugin.getDataFolder() + File.separator
			    				+ "Data.yml");
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
					player.sendMessage(ChatColor.YELLOW + "You placed a " + HLRmain.CHname + ChatColor.YELLOW + "!");
				}
			}
		} else {
			player.sendMessage(HLRmain.CHname + ChatColor.RED + " is not enabled in this world!");
			event.setCancelled(true);
		}	
	}
		
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = (Player) event.getPlayer();
	    String world = event.getBlock().getWorld().getName();
		//player.sendMessage(world);
		if(plugin.isEnabledIn(world))
		{	
			File DataFile = new File(plugin.getDataFolder() + File.separator
					+ "Data.yml");
			YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
		    double x = event.getBlock().getX();
		    double y = event.getBlock().getY();
		    double z = event.getBlock().getZ();
		    //player.sendMessage(String.valueOf(x));   //debug
		    //player.sendMessage(String.valueOf(y));   //debug
		    //player.sendMessage(String.valueOf(z));   //debug
		    String coord = x + "," + y + "," + z;
		    //player.sendMessage(coord);    //debug
			if(DFile.getStringList(world).contains(coord))
			{
				
				Map.Entry<World, Chunk> entry = new AbstractMap.SimpleEntry<World, Chunk>(event.getBlock().getWorld(), event.getBlock().getLocation().getChunk());
				blockInfo.get(entry).remove(event.getBlock().getLocation());
				
			    List<String> coordlist = DFile.getStringList(world);
			    //plugin.log.info(coord);   //debug
			    coordlist.remove(coord);
			    DFile.set(world, coordlist);
			    event.setCancelled(true);
			    event.getBlock().breakNaturally();
			    player.sendMessage(ChatColor.RED + "You destroyed a " + HLRmain.CHname + ChatColor.RED + "!");
			    try {
		            DFile.save(plugin.getDataFolder() + File.separator
		    				+ "Data.yml");
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		}
	}
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event){
		ItemStack item = event.getEntity().getItemStack();
		ItemStack sitem = item.clone();
		sitem.setAmount(1);
		//plugin.log.info("triggered");  //debug
		//plugin.log.info(item.getType().toString());   //debug
	    World world = event.getEntity().getWorld();
	    String worldname = world.getName();
		//plugin.log.info(worldname);   //debug
		if(ConfigHandler.itemList.contains(sitem) && plugin.isEnabledIn(worldname)){
			World w = event.getEntity().getWorld();
			Chunk c = event.getEntity().getLocation().getChunk();
			//plugin.log.info(event.getEntity().getLocation().getChunk().toString());   //debug
			Map.Entry<World, Chunk> entry = new AbstractMap.SimpleEntry<World, Chunk>(w, c);
			boolean notinChunk = false;
			List<Location> loc = null;
			try {
				loc = blockInfo.get(entry);	
				loc.size();   //check null or not
			} catch (NullPointerException e) {
				notinChunk = true;
			}
			if(!notinChunk) {
				for(int i = 0; i < loc.size(); i++) {
					Hopper hopper = null;
					boolean retry = false;
					try {
						hopper = (Hopper) loc.get(i).getBlock().getState();	
					} catch (ClassCastException e) {
						File DataFile = new File(plugin.getDataFolder() + File.separator
								+ "Data.yml");
						YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
						String coord = loc.get(i).getBlockX() + ".0,"  + loc.get(i).getBlockY() + ".0," + loc.get(i).getBlockZ() + ".0";
						blockInfo.get(entry).remove(loc.get(i));
						//plugin.log.info(coord);  //debug
						List<String> tmp = DFile.getStringList(worldname);
						tmp.remove(coord);
						DFile.set(worldname, tmp);
					    try {
				            DFile.save(plugin.getDataFolder() + File.separator
				    				+ "Data.yml");
				        } catch (IOException e1) {
				            e.printStackTrace();
				        }
						retry = true;
					}
					if(!retry) {
						Inventory hopperInv = hopper.getInventory();
						if(hopperInv.firstEmpty() != -1){
							//plugin.log.info("Hopper got space");   //debug
							event.getEntity().remove();
							hopperInv.addItem(item);
							break;
						}
					}
				}
			}
		}
	}
}
