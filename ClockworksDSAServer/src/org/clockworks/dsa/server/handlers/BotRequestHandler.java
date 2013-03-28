package org.clockworks.dsa.server.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.environment.EnvironmentSegment;
import org.clockworks.dsa.server.singletons.CodeQueue;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for pings from bot (RTP Pings)
 *
 */

public class BotRequestHandler implements HttpHandler {
	
	private HashMap<String,BotRequestHandler.TimeOut> timers;
	private final int timeoutDuration = 10000;
	
	public BotRequestHandler(){
		super();
		timers = new HashMap<String,BotRequestHandler.TimeOut>();
	}
	
	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		
		int statusCode = 0;
		String responseBody = "";
		Headers headers = httpExchange.getRequestHeaders();
		List<String> environmentIdList = headers.get("Environment-Id");
		List<String> segmentIdList = headers.get("Segment-Id");
		
		try{
		
			if(environmentIdList==null||segmentIdList==null)
			{
				System.out.println("400: Missing required headers");
				statusCode = 400;
				responseBody = "Missing required headers.";
			}else{
				
				int environmentId = Integer.parseInt(environmentIdList.get(0));
				int segmentId = Integer.parseInt(segmentIdList.get(0));
				
				if(httpExchange.getRequestMethod().equalsIgnoreCase("POST"))
				{
					
					if(environmentId!=0)
					{
						System.out.println("Looking for environment");

						BufferedReader br = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
						
						StringBuilder sb = new StringBuilder();
						String line;
						
						while ((line = br.readLine()) != null) {
							sb.append(line + '\n');
						}
						br.close();
		
						String results = sb.toString();
						
						
						// log results to environment
						Environment e = EnvironmentList.sharedInstance().getEnvironmentById(environmentId);
						if(e==null){
							System.out.println("404: Environment Not Found"); 
							statusCode = 404;
							responseBody = "Environment Not Found.";
						}else{
							
							List<String> validHeader = headers.get("Script-Valid");
							boolean valid = true;
							if(validHeader!=null)
							{
								valid = Boolean.parseBoolean(validHeader.get(0));
							}
							
							int resultInputStatusCode = e.completeSegmentWithResults(results, segmentId, valid);
							if(resultInputStatusCode==404){
								System.out.println("404: Segment Not Found");
								statusCode = 404;
								responseBody = "Segment Not Found.";
							}else if(resultInputStatusCode==409){
								System.out.println("409: Clash of results");
								statusCode = 409;
								responseBody = "Clash of results.";
							}else{
								deleteTimerForIds(environmentId, segmentId);
								System.out.println("Results received:"+results.toString());
							}
						}
					}
				
				
					
					if(statusCode==0)
					{
						if(CodeQueue.sharedInstance().isEmpty())
						{
							statusCode = 204;
							responseBody = "Code unavailable at this time.";
							System.out.println("204: Code unavailable at this time");
						}
						else
						{
							// get from code queue
							EnvironmentSegment seg = CodeQueue.sharedInstance().popFromQueue();
							httpExchange.getResponseHeaders().add("Environment-Id", ""+seg.getParentId());
							httpExchange.getResponseHeaders().add("Segment-Id", ""+seg.getId());
							
							// package into unified object
							responseBody = seg.getUnifiedFile();
							statusCode = 200;
							System.out.println("200: Code sent.");
							createTimerForEnvironmentSegment(seg);
						}
					}
				}
				else if(httpExchange.getRequestMethod().equalsIgnoreCase("GET"))
				{
					
					try{
						resetTimerForIds(environmentId, segmentId);
						statusCode = 200;
						System.out.println("200: Timer reset");
					}catch(Exception e){
						statusCode = 404;
						System.out.println("404: Segment Not Found");
					}
					
				
				}else{
					statusCode = 405;
					responseBody = "Method not allowed";
				}
			}
		}
		catch(Exception e)
		{
			statusCode = 500;
			System.out.println("500: Server Error");
		}
		
		if(statusCode == 204)
		{
			httpExchange.sendResponseHeaders(statusCode, -1);
		}
		else
		{
			httpExchange.sendResponseHeaders(statusCode, responseBody.length());
			httpExchange.getResponseBody().write(responseBody.getBytes());
		}
		httpExchange.close();
		
	}
	
	private void timerDidTimeOut(EnvironmentSegment s){
		System.out.println("Timer time out: Readded segment to queue.");
		CodeQueue.sharedInstance().addToQueue(s);
	}
	
	class TimeOut extends Timer{
		EnvironmentSegment segment;
		public TimeOut(EnvironmentSegment s){
			segment = s;
		}
	}
	private void createTimerForEnvironmentSegment(final EnvironmentSegment s){
		
		TimeOut newTimer = new TimeOut(s);
		newTimer.schedule(new TimerTask(){
			
			@Override
			public void run() {
				timerDidTimeOut(s);
				this.cancel();
			}
			
		},timeoutDuration);
		
		timers.put(""+s.getParentId()+s.getId(), newTimer);
		
	}
	private void resetTimerForIds(int environmentId, int segmentId){
		EnvironmentSegment s = deleteTimerForIds(environmentId, segmentId);
		
		createTimerForEnvironmentSegment(s);
	}

	private EnvironmentSegment deleteTimerForIds(int environmentId, int segmentId){
		TimeOut timer = timers.get(""+environmentId+segmentId);
		if(timer!=null){
			timer.cancel();
			timers.remove(""+environmentId+segmentId);
		}
		return timer.segment;
		
	}

}

