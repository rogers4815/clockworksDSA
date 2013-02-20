package org.clockworks.dsa.server.pings;

import com.sun.net.httpserver.HttpExchange;

public class RTPPing {
	
	private long timestamp;
	private HttpExchange ping;
	
	public RTPPing(HttpExchange ping) {
		timestamp = System.currentTimeMillis();
		this.ping = ping;
	}

	public long getAgeInMillis(){
		return System.currentTimeMillis()-timestamp;
	}
}
