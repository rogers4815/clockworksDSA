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
		HttpURLConnection sendConnection = null;
		try {
			URL serverURL = new URL("http://" + server + ":50080/botrequesthandler"); //throws MalformedURLException
			
			//Send results onto server if they exist
			
			if(environmentID == null){
				environmentID = "0";
			}
			
			if(segmentID == null){
				segmentID = "0";
			}
			
			sendConnection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
			sendConnection.setReadTimeout(10000);
			sendConnection.setRequestMethod("POST");
			
			sendConnection.addRequestProperty("Environment-Id", environmentID);
			sendConnection.addRequestProperty("Segment-Id", segmentID);

			OutputStream output = sendConnection.getOutputStream();
			if(results != null){
				byte[] byteResults = results.getBytes();
				
				//Send request to server
				output.write(byteResults);
				output.flush();

				output.close();
			}
	        
	        sendConnection.connect();
	        int responseCode = sendConnection.getResponseCode();
	        
	        if(responseCode == 200){
	        	environmentID = sendConnection.getHeaderField("Environment-Id");
	        	segmentID = sendConnection.getHeaderField("Segment-Id");
	        	InputStream input = sendConnection.getInputStream();
		        
		        byte[] in = {}, result = {};

		        input.read(in);
		        while(in.length != 0){
		        	byte[] copy = result;
		        	//Add in to the end of result
		        	result = new byte[copy.length + in.length];
		        	System.arraycopy(copy, 0, result, 0, copy.length);
		        	System.arraycopy(in, 0, result, copy.length, in.length);
		        	in = new byte[0];
		        }
		        Log.v("Result", new String(result));
		        return new RTPResponse(responseCode, result, environmentID, segmentID, myContext);
	        }
	        else{
	        	return new RTPResponse(responseCode, null, "", "", myContext);
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
		return new RTPResponse(0, null, "", "", myContext);
	}

	//Send ping to server to reset ping
	public boolean sendRTOPing(String environmentID, String segmentID){
		try {
			URL serverURL = new URL("http://" + server + ":50080/botrequesthandler");		//throws MalformedURLException
			HttpURLConnection connection = (HttpURLConnection) serverURL.openConnection(); //throws IOException
			
			//connection.setDoOutput(true); //Set connection for output only
			connection.setRequestMethod("GET");
			
			connection.addRequestProperty("Environment-Id", environmentID);
			connection.addRequestProperty("Segment-Id", segmentID);
			
			Log.v("Result", ""+connection.getResponseCode());
			
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
