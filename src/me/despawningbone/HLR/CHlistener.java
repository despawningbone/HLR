package me.despawningbone.HLR;

import java.io.*;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
			if(old || (inMain && PHand == EquipmentSlot.HAND) || (inOff && PHand == EquipmentSlot.OFF_HAND)) {
				File DataFile = new File(plugin.getDataFolder() + File.separator
						+ "Data.yml");
				YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
				if(plugin.isEnabledIn(world)) {
				    double x = event.getBlock().getX();
				    double y = event.getBlock().getY();
				    double z = event.getBlock().getZ();
				    //player.sendMessage(String.valueOf(x));   //debug
				    //player.sendMessage(String.valueOf(y));   //debug
				    //player.sendMessage(String.valueOf(z));   //debug
				    String coord = x + "," + y + "," + z;
				    //player.sendMessage(coord);    //debug
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
				} else {
					player.sendMessage(HLRmain.CHname + ChatColor.RED + " is not enabled in this world!");
					event.setCancelled(true);
				}	
			}
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
			    List<String> coordlist = DFile.getStringList(world);
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
			File DataFile = new File(plugin.getDataFolder() + File.separator
					+ "Data.yml");
			YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
			List<String> list = DFile.getStringList(worldname);
			//plugin.log.info("this item is in the list.");   //debug
			for (int i = 0; i < list.size(); i++) {
				double itemx = event.getEntity().getLocation().getX();
				double itemz = event.getEntity().getLocation().getZ();
				String coord = list.get(i);
				//plugin.log.info(coord);   //debug
				String[] coords =  coord.split(",");
				double blockx = Double.parseDouble(coords[0]);
				double blocky = Double.parseDouble(coords[1]);
				double blockz = Double.parseDouble(coords[2]);
				//plugin.log.info(String.valueOf(blockx));   //debug
				//plugin.log.info(String.valueOf(blocky));   //debug
				//plugin.log.info(String.valueOf(blockz));   //debug
				//plugin.log.info(String.valueOf(itemx));   //debug
				//plugin.log.info(String.valueOf(itemz));   //debug
				double bchunkx = Math.floor(blockx / 16);
				double bchunkz = Math.floor(blockz / 16);
				double ichunkx = Math.floor(itemx / 16);
				double ichunkz = Math.floor(itemz / 16);
				Chunk blockChunk = world.getChunkAt((int) bchunkx,(int) bchunkz);
				Chunk itemChunk = world.getChunkAt((int) ichunkx,(int) ichunkz);
				//plugin.log.info(itemChunk.toString()); //debug
				//plugin.log.info(blockChunk.toString());  //debug
				//plugin.log.info(String.valueOf(itemChunk.equals(blockChunk))); //debug
				if(itemChunk == blockChunk){
					//plugin.log.info("found hopper in chunk of item");   //debug
					Location blockcoord = new Location(world , blockx, blocky, blockz);
					//plugin.log.info(blockcoord.getBlock().getType().toString());   //debug
					Block block = blockcoord.getBlock();
					Hopper hopper = null;
					boolean retry = false;
					try {
						hopper = (Hopper) block.getState();	
					} catch (ClassCastException e) {
						list.remove(coord);
						DFile.set(worldname, list);
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
