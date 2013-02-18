package org.clockworks.dsa.server.handlers;

import java.io.IOException;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.environment.EnvironmentSegment;
import org.clockworks.dsa.server.singletons.CodeQueue;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class EnvironmentHandler implements HttpHandler {

	
	@Override
	public void handle(HttpExchange ping) throws IOException {
		// TODO look down
		Environment env = new Environment(null,null);
		// TODO look up
		EnvironmentList.sharedInstance().addEnvironment(env);
		EnvironmentSegment[] segments = env.getSegments(0, env.getSegmentCount());
		for(EnvironmentSegment s : segments){
			CodeQueue.sharedInstance().addToQueue(s);
		}
		// TODO respond to user with response
	}

}
