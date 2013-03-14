package org.clockworks.dsa.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ServerContacter {
	private String server;
	private Context myContext;
	WifiManager wifi;
    public static final String TAG = "Results";
	
	public ServerContacter(String server, Context context){
		this.server = server;
		this.myContext = context;
		wifi = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	//Send RTP ping to server and fetch response
	public RTPResponse sendRTPPing(String results, String environmentID, String segmentID){
		try {
			URL serverURL = new URL("http://" + server + ":50080/botrequesthandler"); //throws MalformedURLException
			
			//Send results onto server if they exist
			if(results != null){
				HttpURLConnection sendConnection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
				
				sendConnection.setRequestMethod("POST");
				
				//Can the server parse these back out properly?
				sendConnection.addRequestProperty("Environment-Id", environmentID);
				sendConnection.addRequestProperty("Segment-Id", segmentID);
						
				OutputStream output = sendConnection.getOutputStream();

				byte[] byteResults = results.getBytes();

				//Send request to server
				output.write(byteResults);
				output.flush();

				output.close();
			}
			//Send RTP ping requesting new simulation to run
			HttpURLConnection receiveConnection = (HttpURLConnection) serverURL.openConnection();

			receiveConnection.setRequestMethod("GET");
	        receiveConnection.setReadTimeout(10000);
	        
	        receiveConnection.connect();
	        int responseCode = receiveConnection.getResponseCode();
	        Log.v("Response code", Integer.toString(responseCode));
	        
	        if(responseCode == 200){
	        	InputStream input = receiveConnection.getInputStream();
		        
		        byte[] in = null, result = null;

		        input.read(in);
		        while(in != null){
		        	Log.v(TAG, in.toString());
		        	byte[] copy = result;
		        	//Add in to the end of result
		        	result = new byte[copy.length + in.length];
		        	System.arraycopy(copy, 0, result, 0, copy.length);
		        	System.arraycopy(in, 0, result, copy.length, in.length);
		        }
		        return new RTPResponse(responseCode, result);
	        }
	        else{
	        	return new RTPResponse(responseCode, null);
	        }
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
			URL serverURL = new URL("http://" + server + ":50080");		//throws MalformedURLException
			HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
			
			connection.setDoOutput(true); //Set connection for output only
			connection.setRequestMethod("POST");
			
			OutputStream output = connection.getOutputStream();
			
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
