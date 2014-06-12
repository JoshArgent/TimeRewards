package com.joshargent.TimeRewards;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class TimeRewards extends JavaPlugin implements Listener {
	
	FileConfiguration config;
	Backend backend;
	Map<UUID, PlaySession> times = new HashMap<UUID, PlaySession>();
	List<Reward> rewards = new ArrayList<Reward>();
	BukkitTask timer;
	
	public void onEnable()
	{
		File file = new File(getDataFolder(), "config.yml");
		if(!file.exists())
		{
			this.saveDefaultConfig();
		}
		config = getConfig();
		
		backend = new Backend(this);
		
		loadRewards();
		
		getServer().getPluginManager().registerEvents(this, this);
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			times.put(p.getUniqueId(), new PlaySession(System.currentTimeMillis()));
		}
		
		timer = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			@Override
			public void run()
			{
				saveProcedure();
			}
			
		}, 0, 20 * 60);
	}

	public void onDisable()
	{
		timer.cancel();
		saveProcedure();
	}
	
	private void saveProcedure()
	{
		for(UUID id : times.keySet())
		{
			PlaySession session = times.get(id);
			long inc = session.update();
			String uuid = id.toString();
			long current = backend.getPlayTime(uuid);
			long newTime = current + inc;
			backend.setPlayTime(uuid, newTime);		
			for(final Reward reward : rewards)
			{
				if(newTime >= reward.getTimeRequirement())
				{
					if(!backend.hasPlayerGotReward(uuid, reward.getName()))
					{
						final Player p = Bukkit.getPlayer(id);
						if(p != null)
						{
							Bukkit.getScheduler().runTask(this, new Runnable(){
								@Override
								public void run() {
									reward.giveReward(p);
								}
							});
							backend.addPlayerReward(uuid, reward.getName());
						}
					}
				}
			}
		}
		backend.saveData();
	}
	
	private void loadRewards()
	{
		rewards.clear();
		Set<String> r1 = config.getKeys(false);
		for(String t1 : r1)
		{
			if(config.isSet(t1 + ".time") && config.isSet(t1 + ".message") && config.isSet(t1 + ".broadcast") && config.isSet(t1 + ".command"))
			{
				int time = config.getInt(t1 + ".time");
				String message = config.getString(t1 + ".message");
				String broadcast = config.getString(t1 + ".broadcast");
				String command = config.getString(t1 + ".command");
				rewards.add(new Reward(t1, time, message, broadcast, command));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void playerJoin(PlayerJoinEvent event)
	{
		times.put(event.getPlayer().getUniqueId(), new PlaySession(System.currentTimeMillis()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private void playerQuit(PlayerQuitEvent event)
	{
		if(times.containsKey(event.getPlayer().getUniqueId()))
		{
			PlaySession session = times.get(event.getPlayer().getUniqueId());
			long inc = session.update();
			String uuid = event.getPlayer().getUniqueId().toString();
			long current = backend.getPlayTime(uuid);
			long newTime = current + inc;
			backend.setPlayTime(uuid, newTime);
			times.remove(event.getPlayer().getUniqueId());
		}
	}
	
}
