package me.despawningbone.HLR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import me.despawningbone.HLR.HLRmain;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class ConfigHandler {
	
	private HLRmain plugin;
	
	private FileConfiguration config;
	private File configFile;
	double fee;
	boolean useEco;
	boolean useCrops;
	boolean useMobDrops;
	boolean usePerms;
	static long time;
	boolean cooldown;
	public static boolean retainTweak = true;
	public static boolean silk;
	public static boolean toInv;
	public static boolean greedy;
	
	public static String prefix;
	public static HashMap<String, String> msgMap = new HashMap<String, String>(); 
	List<String> customitems;
	String tempname; 
	List<String> hopperlore;
	int maxamount;
	public static int chunkHopperLimit;
	
	public static ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
	public static boolean pistonAllow;
	public static List<String> strPistonBlacklist;
	public static boolean waterAllow;
	public static ArrayList<Material> waterBlacklist = new ArrayList<Material>();
	public static boolean spawnerAllow;
	List<String> strSpawnerWhitelist;
	public static ArrayList<EntityType> spawnerWhitelist = new ArrayList<EntityType>();
	public static boolean cactusAllow;
	
	public static Logger log = HLRmain.log;   //debug
	
	/**
	 * Constuctor for ConfigHandler, Runs the createConfig() and getConfigValue() methods.
	 */
	public ConfigHandler(HLRmain instance) {
		plugin = instance;
		config = plugin.getConfig();
		createConfig();
	}
	
	/**
	 * Copys configuration from defaults and makes it into a file.
	 */
	public void createConfig() {
		File configFile = new File(plugin.getDataFolder() + File.separator
				+ "config.yml");
		if (!configFile.exists()) {
			// Tells console its creating a config.yml
			HLRmain.log.info("Cannot find config.yml, Generating now....");
			plugin.saveDefaultConfig();
			HLRmain.log.info("Config generated!");
		}
	}
	
	public void initDataFile() {
		File DataFile = new File(plugin.getDataFolder() + File.separator
				+ "Data.yml");
		if (!DataFile.exists()) {
			// Tells console its creating a Data.yml
			HLRmain.log.info("Cannot find Data.yml, Generating now....");
			try {
				DataFile.createNewFile();
				HLRmain.log.info("Data file generated!");
			} catch (IOException e) {
				HLRmain.log.severe("Cannot generate Data.yml, reason: " + e.getMessage());
			}
		} else {	
			YamlConfiguration DFile = YamlConfiguration.loadConfiguration(DataFile);
			Iterator<String> i = DFile.getKeys(true).iterator();
			while(i.hasNext()) {
				String key = (String)i.next();
				List<String> coords = DFile.getStringList(key);
				List<Location> buffer = new ArrayList<Location>();
				World world = Bukkit.getServer().getWorld(key);
				if(world == null) {
					HLRmain.log.info("Could not load the world " + key + "! Skipping...");
					continue;
				}
				for(int n = 0; n < coords.size(); n++) {
					String[] c =  coords.get(n).split(",");
					double x = Double.parseDouble(c[0]);
					double y = Double.parseDouble(c[1]);
					double z = Double.parseDouble(c[2]);
					Location loc = new Location(world, x, y, z);
					buffer.add(loc);
					
				}
				/*for(int d = 0; d < buffer.size(); d++) {   //debug
					plugin.log.info("Buffer: " + buffer.get(d).toString());
				}*/
				for(int n = 0; n < buffer.size(); n++) {
					Location loc = buffer.get(n);
					Map.Entry<UUID, String> entry = new AbstractMap.SimpleEntry<UUID, String>(world.getUID(), loc.getChunk().toString());
					if (!CHlistener.blockInfo.containsKey(entry)) {
					    List<Location> list = new ArrayList<Location>();
					    list.add(loc);

					    CHlistener.blockInfo.put(entry, list);
					} else {
					    CHlistener.blockInfo.get(entry).add(loc);
					}
					//HLRmain.log.info(entry.getKey().toString());   //debug
					//HLRmain.log.info(entry.getValue());   //debug
					/*for(int d = 0; d < CHlistener.blockInfo.get(entry).size(); d++) {   //debug
						plugin.log.info(CHlistener.blockInfo.get(entry).get(d).toString());
					}*/
				}
				//HLRmain.log.info("Looped");
			}
		}
	}

	
	/**
	 * Reloads the configuration and sends the sender a message.
	 *  
	 * @param sender CommandSender player/console
	 * @param message String to send on completion
	 */
	public void reloadConfig(CommandSender sender, String message) {
		plugin.reloadConfig();
		config = plugin.getConfig();
		getConfigValues();
		initConfigValues();
		initMsgFile();
		sender.sendMessage(message);
	}
	
	/**
	 * Gets the config from the plugin.
	 * 
	 * @return the Configuration
	 */
	public FileConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Gets the actual file from the system.
	 * 
	 * @return the Configuration File
	 */
	public File getConfigFile() {
		return configFile;
	}
	
	/**
	 * Gets all configuration values
	 */
	@SuppressWarnings("unused")
	public void initMsgFile() {  //known bug: if the messages keys are updated in config it will just show null
		File MsgFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");
		if(!MsgFile.exists()) {
			HLRmain.log.info("Cannot find messages.yml, Generating now....");
			try {
				MsgFile.createNewFile();
			    InputStream defMsgFile = plugin.getResource("messages.yml");
			    Files.copy(defMsgFile, Paths.get(MsgFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
			    defMsgFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			HLRmain.log.info("Message file generated!");
		}
		InputStream def = plugin.getResource("messages.yml");
		YamlConfiguration defcfg = YamlConfiguration.loadConfiguration(new InputStreamReader(def));
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(MsgFile);
		if(!defcfg.getKeys(true).equals(cfg.getKeys(true))) {
			HLRmain.log.warning("Message File's keys are not the same.");
			HLRmain.log.warning("This can mean that your configuration file is corrupted or was tempered with wrongly.");
			HLRmain.log.warning("Please reset or remove the message file in order for it to work properly.");
		}
		prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Prefix"));
		ConfigurationSection cfgSec = cfg.getConfigurationSection("Msgs");
		//TODO maybe add a check for keys so that whenever its changed alert the user
		Map<String, Object> values = cfgSec.getValues(true);
		for(Entry<String,Object> val : values.entrySet()) {
			try {
				MemorySection temp = (MemorySection) val.getValue();	
			} catch (ClassCastException e) {
				String valStr = (String) val.getValue();
				String msg = ChatColor.translateAlternateColorCodes('&', valStr);
				//Feel free to suggest any placeholders
				msg = msg.replaceAll("%hoppername%", HLRmain.CHname).replaceAll("%fee%", String.valueOf(fee))
						.replaceAll("%maxamount%", String.valueOf(maxamount)).replaceAll("%chunklimit%", String.valueOf(chunkHopperLimit));
				msgMap.put(val.getKey(), msg);
			}
		}
	}
	
	public void getConfigValues() {
		YamlConfiguration defcfg = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
		if(!defcfg.getKeys(true).equals(config.getKeys(true))) {
			HLRmain.log.warning("Config File's keys are not the same.");
			HLRmain.log.warning("This can mean that your configuration file is corrupted or was tempered with wrongly.");
			HLRmain.log.warning("Please reset or remove the config file in order for it to work properly.");
		}
		HLRmain.enabledWorlds = config.getStringList("Enabled-in-worlds");
		useEco = config.getBoolean("Eco.Use");
		fee = config.getDouble("Eco.Conversion-fee");
		customitems = config.getStringList("ItemList.Custom-items");
		usePerms = config.getBoolean("Use-permissions");
		tempname = config.getString("Hopper-name");
		time = config.getLong("Cooldown.Seconds") * 20;
		cooldown = config.getBoolean("Cooldown.Enable");
		hopperlore = config.getStringList("Hopper-lore");
		useMobDrops = config.getBoolean("ItemList.Mob-drops");
		useCrops = config.getBoolean("ItemList.Crops");
		maxamount = config.getInt("Max-amount");
		chunkHopperLimit = config.getInt("Chunk-HopperLimit");
		
		greedy = config.getBoolean("Greedy-mode");
		retainTweak = config.getBoolean("Retain-tweaked");
		silk = config.getBoolean("Silk-only");
		toInv = config.getBoolean("To-inv");
		
		pistonAllow = config.getBoolean("Farm-tweaks.Piston-farms.Allow");
		strPistonBlacklist = config.getStringList("Farm-tweaks.Piston-farms.Blacklist");
		waterAllow = config.getBoolean("Farm-tweaks.Water-farms.Allow");
		strSpawnerWhitelist = config.getStringList("Farm-tweaks.Spawners.Whitelist ");
		spawnerAllow = config.getBoolean("Farm-tweaks.Spawners.Allow");
		cactusAllow = config.getBoolean("Farm-tweaks.Cactus-farms");
		
		
	}
	
	public void initConfigValues() {
		//log.info("setting up economy...");  //debug
				//log.info(("useEco = " + String.valueOf(useEco)));  //debug
				//log.info(String.valueOf(fee));
				if (useEco) {
					if (!plugin.setupEconomy()) {
						log.severe("Disabling due to no Vault dependency or valid economy plugin found!");
						plugin.getServer().getPluginManager().disablePlugin(plugin);
						return;
					}
				}
				HLRmain.CHname = ChatColor.translateAlternateColorCodes('&', tempname);
				//log.info(String.valueOf(time));   //debug
				//log.info(HLRmain.CHname);    //debug
				//log.info(("useCrops = " + String.valueOf(useCrops)));  //debug
				//log.info(("useMobDrops = " + String.valueOf(useMobDrops)));  //debug
				if(!itemList.isEmpty()){
					itemList.clear();
					//log.info("itemList cleared");  //debug
				}
				if(useCrops){
					//crops
					//log.info("adding crops to itemlist...");      //debug
					itemList.add(new ItemStack(Material.PUMPKIN)); itemList.add(new ItemStack(Material.CACTUS)); itemList.add(new ItemStack(Material.WHEAT)); 
					itemList.add(new ItemStack(Material.CARROT_ITEM)); itemList.add(new ItemStack(Material.SUGAR_CANE)); itemList.add(new ItemStack(Material.MELON));
					itemList.add(new ItemStack(Material.SEEDS)); itemList.add(new ItemStack(Material.POTATO_ITEM)); itemList.add(new ItemStack(Material.POISONOUS_POTATO)); 
					itemList.add(new ItemStack(Material.RED_MUSHROOM)); itemList.add(new ItemStack(Material.BROWN_MUSHROOM)); itemList.add(new ItemStack(Material.NETHER_STALK));
					
				}
				if(useMobDrops){
					//mob drops
					//log.info("adding mobdrops to itemlist...");   //debug
					itemList.add(new ItemStack(Material.FEATHER)); itemList.add(new ItemStack(Material.RAW_CHICKEN)); itemList.add(new ItemStack(Material.LEATHER)); itemList.add(new ItemStack(Material.SPIDER_EYE));
					itemList.add(new ItemStack(Material.ENDER_PEARL)); itemList.add(new ItemStack(Material.RAW_BEEF)); itemList.add(new ItemStack(Material.PORK)); itemList.add(new ItemStack(Material.SLIME_BALL));
					itemList.add(new ItemStack(Material.WOOL)); itemList.add(new ItemStack(Material.ARROW)); itemList.add(new ItemStack(Material.SULPHUR)); itemList.add(new ItemStack(Material.GOLD_NUGGET));
					itemList.add(new ItemStack(Material.IRON_INGOT)); itemList.add(new ItemStack(Material.MUTTON)); itemList.add(new ItemStack(Material.BONE)); itemList.add(new ItemStack(Material.INK_SACK)); 
					itemList.add(new ItemStack(Material.BLAZE_ROD)); itemList.add(new ItemStack(Material.ROTTEN_FLESH)); itemList.add(new ItemStack(Material.STRING)); itemList.add(new ItemStack(Material.PRISMARINE_SHARD));
					itemList.add(new ItemStack(Material.PRISMARINE_CRYSTALS)); itemList.add(new ItemStack(Material.RAW_FISH));
				}
				if (!customitems.isEmpty()) {
					for (int i=0;i < customitems.size();i++)
					{
						String item = customitems.get(i);
						String itemname = null; String dv = null;
						boolean nodv = false;
						try {
							itemname = item.split(":")[0];
							dv = item.split(":")[1];
						} catch (ArrayIndexOutOfBoundsException e) {
							itemname = item;
							nodv = true;
						}
						itemname = itemname.toUpperCase();
						Material material = Material.getMaterial(itemname);
						if(nodv) {	
							itemList.add(new ItemStack(material));
						} else {
							itemList.add(new ItemStack(material, 1, (short)Short.parseShort(dv)));
						}
					}
				} /* else {
				    //log.info("custom item list is Empty"); // debug
				} */
				if(!waterBlacklist.isEmpty()) waterBlacklist.clear();
				waterBlacklist = initMaterialList(config.getStringList("Farm-tweaks.Water-farms.Blacklist"));		
				if(!spawnerWhitelist.isEmpty()) spawnerWhitelist.clear();
				for (int i=0;i < strSpawnerWhitelist.size();i++)
				{
					String strType = strSpawnerWhitelist.get(i);
					try {
						EntityType type = EntityType.valueOf(strType.trim());
						spawnerWhitelist.add(type);	
					} catch (IllegalArgumentException e) {
						log.info("Unknown Entity type " + strType + "! Skipping...");
					}
				}
				
				if(!HLRmain.hopperlore.isEmpty()){
					HLRmain.hopperlore.clear();
					//log.info("itemList cleared");
				}
				if (!hopperlore.isEmpty()) {
					for (int i=0;i < hopperlore.size();i++)
					{
						//log.info(String.valueOf(hopperlore.size()));
						String lore = hopperlore.get(i);
						lore = ChatColor.translateAlternateColorCodes('&', lore);
						//log.info(("hopperlore: " + String.valueOf(lore)));  //debug
						HLRmain.hopperlore.add(lore);
					}
				}
	}
	
	private static ArrayList<Material> initMaterialList(List<String> input) {
		if (!input.isEmpty()) {
			ArrayList<Material> output = new ArrayList<Material>();
			for (int i=0;i < input.size();i++)
			{
				String strMat = input.get(i);
				Material mat = Material.getMaterial(strMat);
				output.add(mat);
			}
			return output;
		}
		return new ArrayList<Material>();
	}
}
