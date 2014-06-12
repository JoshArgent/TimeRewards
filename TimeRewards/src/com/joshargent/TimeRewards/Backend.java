package com.joshargent.TimeRewards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Backend {
	
	private FileConfiguration data;
	private File dataFile;
	
	public Backend(TimeRewards plugin)
	{
		dataFile = new File(plugin.getDataFolder(), "players.yml");
		if(!dataFile.exists())
		{
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		data = new YamlConfiguration();
		try {
			data.load(dataFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayTime(String uuid, long time)
	{
		data.set(uuid + ".t", time);
	}
	
	public long getPlayTime(String uuid)
	{
		if(data.contains(uuid + ".t"))
		{
			return data.getLong(uuid + ".t");
		}
		return 0L;
	}
	
	public void addPlayerReward(String uuid, String reward)
	{
		if(data.contains(uuid + ".r"))
		{
			data.getStringList(uuid + ".r").add(reward);
		}
		else
		{
			List<String> rewards = new ArrayList<String>();
			rewards.add(reward);
			data.set(uuid + ".r", rewards);
		}
	}
	
	public boolean hasPlayerGotReward(String uuid, String reward)
	{
		if(data.contains(uuid + ".r"))
		{
			return data.getStringList(uuid + ".r").contains(reward);
		}
		else return false;
	}
	
	public void saveData()
	{
		try {
			data.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
