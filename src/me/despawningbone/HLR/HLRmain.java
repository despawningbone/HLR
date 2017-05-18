package me.despawningbone.HLR;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import me.despawningbone.HLR.CHlistener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HLRmain extends JavaPlugin {
	
    public static Economy econ = null;
	public static HLRmain plugin;
	public static Logger log;
	public CHlistener listener = new CHlistener(this);
	public HLRCommandMain HLRCM = new HLRCommandMain(this);
	public HLRSubCommand HLRSC = new HLRSubCommand(this);
	
	public static String ver;
	public static String CHname;
	public static List<String> hopperlore = new ArrayList<String>();
	//public static String CHname = ChatColor.GREEN + "Crop " + ChatColor.WHITE + "Hopper";
	public static List<String> enabledWorlds;
	public ConfigHandler configHandler;
	
	 // tools that can be damaged
	/*public ArrayList<Integer> tools = new ArrayList<Integer>(Arrays.asList(
			256, 257, 258, 259, 267, 268, 269, 270, 271, 272, 273, 274, 275,
			276, 277, 278, 279, 283, 284, 285, 286, 290, 291, 292, 293, 294,
			298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310,
			311, 312, 313, 314, 315, 316, 317, 346, 359)); */
	String[] commandAliases = {"chopper"};
	
	@Override
	public void onDisable() {
		log.info(String.format("Disabled HLR Version %s", getDescription().getVersion()));
	}

	@Override
	public void onEnable() {
		log = getLogger();
		configHandler = new ConfigHandler(this);
		configHandler.createDataFile();
		configHandler.initConfigValues();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		
		ver = this.getServer().getVersion().toString().split("MC: ")[1].replaceAll("\\)", "");
		
	    //log.info(plugin.getServer().getVersion().toString());   //debug
	    //log.info(ver);   //debug
		//log.info("getting commands...");    //debug
		getCommand("converthopper").setExecutor(HLRCM);
		getCommand("converthopper").setAliases(Arrays.asList(commandAliases));
		getCommand("hlr").setExecutor(HLRSC);

		// Print that the plugin has been enabled!
		log.info(String.format("HLR Version: %s by despawningbone has been enabled!", getDescription().getVersion()));
	}

    public boolean isEnabledIn(String world)
    {
        return enabledWorlds.contains(world);
    }
    
	public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }  
        econ = rsp.getProvider();
        return econ != null;
    }
	public static double getMoney(Player p) {
        double m = econ.getBalance(p);
        return m;
    }
}
