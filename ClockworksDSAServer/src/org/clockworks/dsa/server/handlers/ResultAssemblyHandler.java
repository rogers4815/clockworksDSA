package org.clockworks.dsa.server.handlers;

import java.io.IOException;

import org.clockworks.dsa.server.environment.Environment;
import org.clockworks.dsa.server.singletons.EnvironmentList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler listening for pings from user (WOPPings)
 * Sends results if ready, responds with a not-ready message otherwise
 *
 */
public class ResultAssemblyHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange ping) throws IOException {
		// TODO: get the environment id from the WOPPing
		int environmentId = -321364540;
		Environment environment = EnvironmentList.sharedInstance().getEnvironmentById(environmentId);
		if(environment==null){
			// 404 response not found
		}else{
			Object results = environment.returnAssembledResult();
			if(results==null){
				// 102 process not ready
			}else{
				// 200 all good return the stuff
				EnvironmentList.sharedInstance().deleteEnvironment(environment);
			}
		}
		
	}

}
