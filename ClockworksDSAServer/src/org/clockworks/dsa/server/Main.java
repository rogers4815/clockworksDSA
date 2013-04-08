package org.clockworks.dsa.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.clockworks.dsa.server.handlers.BotRequestHandler;
import org.clockworks.dsa.server.handlers.EnvironmentHandler;
import org.clockworks.dsa.server.handlers.ResultAssemblyHandler;

import com.sun.net.httpserver.HttpServer;

/**
 * Main class to run the Simulation Distribution Server
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	    InetSocketAddress addr = new InetSocketAddress(50080);
	    HttpServer server = HttpServer.create(addr, 1000);

	    server.createContext("/botrequesthandler", new BotRequestHandler());
	    server.createContext("/resultassemblyhandler", new ResultAssemblyHandler());
	    server.createContext("/environmenthandler", new EnvironmentHandler());
	    server.setExecutor(Executors.newCachedThreadPool());
	    server.start();
	    System.out.println("Server is listening on port 50080" );
	}

}
