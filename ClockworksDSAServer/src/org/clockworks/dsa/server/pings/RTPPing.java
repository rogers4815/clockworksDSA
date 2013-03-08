package org.clockworks.dsa.server.pings;

import com.sun.net.httpserver.HttpExchange;

/**
 * Ready-to-process ping
 * Ping that is sent from bot to server
 *
 */
public class RTPPing {
	
	private long timestamp;
	
	public RTPPing(HttpExchange ping) {
		timestamp = System.currentTimeMillis();
	}

	public long getAgeInMillis(){
		return System.currentTimeMillis()-timestamp;
	}
}
