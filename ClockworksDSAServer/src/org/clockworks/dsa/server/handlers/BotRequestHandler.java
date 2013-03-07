package org.clockworks.dsa.server.handlers;

import java.io.IOException;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.environment.EnvironmentSegment;
import org.clockworks.dsa.server.pings.RTPPing;
import org.clockworks.dsa.server.singletons.CodeQueue;
import org.clockworks.dsa.server.singletons.EnvironmentList;
import org.clockworks.dsa.server.singletons.PingQueue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for pings from bot (RTP Pings)
 *
 */
public class BotRequestHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange request) throws IOException {
		// if rtpPing, as opposed to Ï'm-not-ready ping
		{
			//if contains results i.e. POST
			{
				// Cancel process timer
				// log results to environment
				Object results = null;
				int environmentId = -3928282;
				int segmentId = -2282920;
				//// TODO: variables
				
				Environment e = EnvironmentList.sharedInstance().getEnvironmentById(environmentId);
				if(e==null){
					System.out.println("404: Environment Not Found"); 
				}
				int statusCode = e.completeSegmentWithResults(results, segmentId);

				if(statusCode==404){
					System.out.println("404: Segment Not Found");
				}else if(statusCode==409){
					System.out.println("409: Clash of results");
				}
			}
			//queue ping
			PingQueue.sharedInstance().addToQueue(new RTPPing(request));
			if(CodeQueue.sharedInstance().isEmpty()){
				// respond 204
			}else{
				
				// get from code queue
				EnvironmentSegment seg = CodeQueue.sharedInstance().popFromQueue();
				// package into unified object
				Object objectToSend = seg.getUnifiedFile();
				// respond
				// instantiate timer 
				// respond 200
			}
		}
		// else, as in Í'm-not-ready ping
		{
			// Reset Process timer
		}
	}

}
