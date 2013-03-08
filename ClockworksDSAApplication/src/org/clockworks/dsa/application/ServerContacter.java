package org.clockworks.dsa.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class ServerContacter {
	private String server;
	private Context myContext;
	WifiManager wifi;

	
	public ServerContacter(String server, Context context){
		this.server = server;
		this.myContext = context;
		wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	//Send RTP ping to server and fetch response
	public RTPResponse sendRTPPing(String results, String environmentID, String segmentID){
		try {
			URL serverURL = new URL(server); //throws MalformedURLException
			HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
			
			//Set connection for input and output
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			//Can the server parse these back out properly?
			connection.setRequestProperty("Environment-Id", environmentID);
			connection.setRequestProperty("Segment-Id", segmentID);
			
			connection.setRequestMethod("GET");
			connection.setReadTimeout(10000);
			
			//Setup OutputStream for sending requests to the server and InputStream for receiving
			BufferedOutputStream output = new BufferedOutputStream(connection.getOutputStream());
			BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
			
			byte[] byteResults = results.getBytes();
			
			//Send request to server
			output.write(byteResults, 0, byteResults.length);
			output.flush();
			
			//Get response and return it
			int readSuccess;
			byte[] response = null, responsePart = null;
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			
			readSuccess = input.read(responsePart);
			while(readSuccess != -1){
				responseStream.write(responsePart);
				readSuccess = input.read(responsePart);
			}
			response = responseStream.toByteArray();
			
			//Tidy up and return
			output.close();
			input.close();
			
			return new RTPResponse(response);
			
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//Send ping to server to reset ping
	public boolean sendRTOPing(){
		try {
			URL serverURL = new URL(server);		//throws MalformedURLException
			HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
			
			connection.setDoOutput(true); //Set connection for output only
			connection.setRequestMethod("POST");
			
			BufferedOutputStream output = new BufferedOutputStream(connection.getOutputStream());
			
			//TODO generate actual RTO ping
			byte[] rtoPing = {0};
			
			output.write(rtoPing, 0, rtoPing.length);
			output.flush();
			output.close();
			return true; //success
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false; //failure
	}
	
	//Check if node is currently on an allowed network
	public boolean onAllowedNetwork(){
		//Get information on WiFi adapter
		WifiInfo info = wifi.getConnectionInfo();
		if(info.getNetworkId() != -1){
			return true;
		}
		else{
			return false;
		}
	}
}
