package org.clockworks.dsa.server.handlers;

import java.io.IOException;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.environment.EnvironmentSegment;
import org.clockworks.dsa.server.singletons.CodeQueue;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for new simulations/environments from user
 *
 */
public class EnvironmentHandler implements HttpHandler {
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int statusCode = 0;
		String responseBody = "";
		if(httpExchange.getRequestMethod().equalsIgnoreCase("POST")){
			int envId;
			do{
				envId = (int)System.currentTimeMillis();
			}while(EnvironmentList.sharedInstance().getEnvironmentById(envId)!=null);
			
			try{
				
				Environment env = new Environment(envId,httpExchange.getRequestBody());
				EnvironmentList.sharedInstance().addEnvironment(env);
				EnvironmentSegment[] segments = env.getSegments(0, env.getSegmentCount());
				for(EnvironmentSegment s : segments){
					CodeQueue.sharedInstance().addToQueue(s);
				}
				statusCode = 200;
				responseBody = ""+envId;
				System.out.println("200 : Environment recieved.");
				
			}catch(Exception e){
				
				statusCode = 400;
				responseBody = "Environment error";
				System.out.println("400 : Environment error");
			}

		}else{
			statusCode = 405;
			System.out.println("405 : Method not allowed");
		}
		httpExchange.sendResponseHeaders(statusCode, responseBody.length());
		httpExchange.getResponseBody().write(responseBody.getBytes());
		httpExchange.close();
		
	}

}
