package me.despawningbone.HLR;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Timer {
	public static int taskid;
    public void main(String args[]) {
        taskid = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HLR"), new Runnable() {
            @Override
            public void run() {
            		HLRCommandMain.start = false;
                    Player player = HLRCommandMain.executor;
                    player.sendMessage(ChatColor.YELLOW + "You can now use /converthopper again.");    	
            }
        }, ConfigHandler.time);
    }
}