package me.despawningbone.HLR;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Timer {
	public static int taskid;
    public void main(Player player) {
        taskid = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("HLR"), new Runnable() {
            @Override
            public void run() {
            		HLRCommandMain.start.put(player, false);
                    player.sendMessage(ConfigHandler.prefix + ConfigHandler.msgMap.get("Timer.CanUseConvertCmd"));    	
            }
        }, ConfigHandler.time);
    }
}
