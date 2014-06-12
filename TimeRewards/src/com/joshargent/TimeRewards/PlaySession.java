package com.joshargent.TimeRewards;

public class PlaySession {
	
	private long start;
	
	public PlaySession(long start)
	{
		this.start = start;
	}
	
	public long update()
	{
		long dif = System.currentTimeMillis() - start;
		start = System.currentTimeMillis();
		return dif;
	}

}
