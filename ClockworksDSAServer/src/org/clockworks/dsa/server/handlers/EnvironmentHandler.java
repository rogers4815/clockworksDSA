package org.clockworks.dsa.server.handlers;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.environment.EnvironmentSegment;
import org.clockworks.dsa.server.singletons.CodeQueue;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import java.io.File;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for new simulations/environments from user
 * 
 */
public class EnvironmentHandler implements HttpHandler {

	private final String DELIMITER = ";;;";

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int statusCode = 0;
		String responseBody = "";
		try {
		    	// POST request indicates attempt to send a new Environment
			if (httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
			    
			    	// Assign the new Environment a unique ID, based on its timestamp
				int envId;
				do {
					envId = (int) System.currentTimeMillis();
				} while (EnvironmentList.sharedInstance().getEnvironmentById(envId) != null);
				PrintStream out = null;
				try {
					
				    	// Parse the stream from the HttpExchange into a string
					BufferedReader br = new BufferedReader(new InputStreamReader(
							httpExchange.getRequestBody()));
					StringBuilder sb = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line + '\n');
					}
					br.close();
	
					// Split this string into part representing script and part representing parameters
					String requestbodyString = sb.toString();
					String[] parameters = requestbodyString.split(DELIMITER);

					File script = new File(envId + "-simulation.py");
					out = new PrintStream(new FileOutputStream(script));
					out.print(parameters[1]);
	
					// Create a new Environment based on this information and get its segments
					Environment env = new Environment(envId, parameters[0], script);
					EnvironmentList.sharedInstance().addEnvironment(env);
					EnvironmentSegment[] segments = env.getSegments(0,env.getSegmentCount());
					
					// Add the new Environment's segments to the Code Queue
					for (EnvironmentSegment s : segments) {
						CodeQueue.sharedInstance().addToQueue(s);
					}
					
					// Indicate to user that Environment was received successfully
					System.out.println(""+segments.length+" segments added to queue");
					statusCode = 200;
					responseBody = "" + envId;
					System.out.println("200 : Environment recieved.");
	
				} catch (Exception e) {
	
					statusCode = 400;
					responseBody = "Environment error";
					System.out.println("400 : Environment error");
					e.printStackTrace();
				}finally{
					out.close();

				}
	
			}
			// Request is something other than a POST and thus invalid
			else {
				statusCode = 405;
				System.out.println("405 : Method not allowed");
			}
		}
		catch(Exception e)
		{
			statusCode = 500;
		}
		
		// Respond appropriately to user
		httpExchange.sendResponseHeaders(statusCode, responseBody.length());
		httpExchange.getResponseBody().write(responseBody.getBytes());
		httpExchange.close();

	}

}
