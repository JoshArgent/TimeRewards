package com.joshargent.TimeRewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Reward {
	
	private long time;
	private String message;
	private String broadcast;
	private String cmd;	
	private String name;
	
	public Reward(String name, double time, String message, String broadcast, String cmd)
	{
		this.time = (long) (time * 3600000); // multiply hours by 1 hour in ms
		this.message = message;
		this.broadcast = broadcast;
		this.cmd = cmd;
		this.name = name;
	}
	
	public long getTimeRequirement()
	{
		return time;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void giveReward(Player player)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), insertPlayerName(cmd, player.getName()));
		String broadcast2 = ChatColor.translateAlternateColorCodes('&', insertPlayerName(broadcast, player.getName()));
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.sendMessage(broadcast2);
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', insertPlayerName(message, player.getName())));
	}
	
	private String insertPlayerName(String cmd, String player)
	{
		if(cmd.contains("%player%"))
		{
			String cmd2 = cmd.replace("%player%", player);
			return cmd2;
		}
		else return cmd;
	}

}
