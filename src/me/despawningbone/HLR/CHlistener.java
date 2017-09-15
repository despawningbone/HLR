package me.despawningbone.HLR;

import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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
	
	public static HashMap<Entry<UUID, String>, List<Location>> blockInfo = new HashMap<Entry<UUID, String>, List<Location>>();
	

	public CHlistener(HLRmain instance) {
		plugin = instance;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = (Player) event.getPlayer();
	    String world = event.getBlock().getWorld().getName();
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
			if(plugin.isEnabledIn(world)) {
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
				    Map.Entry<UUID, String> entry = new AbstractMap.SimpleEntry<UUID, String>(event.getBlock().getWorld().getUID(), event.getBlock().getLocation().getChunk().toString());
				    if(!CHlistener.blockInfo.containsKey(entry) || ConfigHandler.chunkHopperLimit == -1 || CHlistener.blockInfo.get(entry).size() < ConfigHandler.chunkHopperLimit) {
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
						player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("Listener.PlacedHopper"));	
				    } else {
				    	event.setCancelled(true);
				    	player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("Listener.HopperLimitReached"));
				    }
				}
			} else {
				player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("Listener.NotEnabledInWorld"));
				event.setCancelled(true);
			}
		}
	}
		
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = (Player) event.getPlayer();
	    String world = event.getBlock().getWorld().getName();
		//player.sendMessage(world);
		if(plugin.isEnabledIn(world)) {   //Note: it would cause problems if a tweaked hopper is still in disabled world and destroyed and then someone re-enabled the world 	
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
				boolean skip = false;
				Map.Entry<UUID, String> entry = new AbstractMap.SimpleEntry<UUID, String>(event.getBlock().getWorld().getUID(), event.getBlock().getLocation().getChunk().toString());
				try {
					blockInfo.get(entry).remove(event.getBlock().getLocation());
				} catch (NullPointerException e) {
					skip = true;
				}
				if(!skip) {
					List<String> coordlist = DFile.getStringList(world);
				    //plugin.log.info(coord);   //debug
				    coordlist.remove(coord);
				    DFile.set(world, coordlist);
				    event.setCancelled(true);
				    event.getBlock().breakNaturally();
				    player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("Listener.DestroyedHopper"));
				    try {
			            DFile.save(plugin.getDataFolder() + File.separator
			    				+ "Data.yml");
			        } catch (IOException e) {
			            e.printStackTrace();
			        }	
				}
			}
		}
	}
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event){   //suspecting world changes causes disruption
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
			//plugin.log.info(w.getName());   //debug
			//plugin.log.info(c.toString());   //debug
			//plugin.log.info(event.getEntity().getLocation().toString());   //debug
			Map.Entry<UUID, String> entry = new AbstractMap.SimpleEntry<UUID, String>(w.getUID(), c.toString());
			//plugin.log.info("this" + entry.toString());    //debug
			/*for(Entry<UUID, String> e: blockInfo.keySet()) {  //debug
				plugin.log.info("map" + e.toString());
			}*/
			boolean notinChunk = false;
			List<Location> loc = null;
			//plugin.log.info(entry == null ? "entrynull" : entry.toString());   //debug
			//plugin.log.info("map" + String.valueOf(blockInfo.isEmpty()));   //debug
			loc = blockInfo.get(entry);
			try {	
				//plugin.log.info("map" + loc.get(0).toString());    //debug
				loc.size();   //check null or not
			} catch (NullPointerException /*| ArrayIndexOutOfBoundsException*/ e) { 
				notinChunk = true;
				//plugin.log.info("null not in chunk");   //debug
			}
			if(!notinChunk) {
				//plugin.log.info("inchunk");  //debug
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
