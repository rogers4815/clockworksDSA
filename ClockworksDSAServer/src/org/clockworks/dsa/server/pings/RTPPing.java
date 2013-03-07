package org.clockworks.dsa.server.pings;

import com.sun.net.httpserver.HttpExchange;

/**
 * Ready-to-process ping
 * Ping that is sent from bot to server
 *
 */
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
