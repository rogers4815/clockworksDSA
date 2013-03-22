package org.clockworks.dsa.server.handlers;

import java.io.IOException;
import java.util.List;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for pings from user (WOPPings)
 * Sends results if ready, responds with a not-ready message otherwise
 *
 */
public class ResultAssemblyHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		int statusCode = 0;
		String responseBody = "";
		Headers headers = httpExchange.getRequestHeaders();
		List<String> environmentIdList = headers.get("Environment-Id");
		try
		{
		
			if(environmentIdList==null)
			{
				System.out.println("400: Missing required headers");
				statusCode = 400;
				responseBody = "Missing required headers.";
			}else{
				
				int environmentId = Integer.parseInt(environmentIdList.get(0));
				
				if(httpExchange.getRequestMethod().equalsIgnoreCase("GET"))
				{
					Environment environment = EnvironmentList.sharedInstance().getEnvironmentById(environmentId);
					if(environment==null){
						System.out.println("404: Environment not found");
						statusCode = 404;
						responseBody = "Environment not found";
					}else{
						String results = environment.returnAssembledResult();
						if(results==null){
							System.out.println("102: Process not ready");
							statusCode = 102;
							responseBody = "Process not ready";
						}else{
							statusCode = 200;
							responseBody = results;
							EnvironmentList.sharedInstance().deleteEnvironment(environment);
						}
					}
				}
				else{
					System.out.println("405: Method not allowed");
					statusCode = 405;
					responseBody = "Method not allowed";
				}
			}
		}
		catch(Exception e)
		{
			statusCode = 500;
		}
		
		if(statusCode==102)
		{
			httpExchange.sendResponseHeaders(statusCode, -1);
			httpExchange.close();
		}
		else
		{
			httpExchange.sendResponseHeaders(statusCode, responseBody.length());
			httpExchange.getResponseBody().write(responseBody.getBytes());
			httpExchange.close();
		}
		
	}

}
