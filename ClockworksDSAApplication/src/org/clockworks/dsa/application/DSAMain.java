package org.clockworks.dsa.application;

import android.content.Context;
import android.util.Log;

public class DSAMain extends Thread{
	private String simulationResults, environmentID, segmentID;
	private boolean done;
	private ServerContacter requester;
	private PythonProcessor processor;
	
	public DSAMain(String server, Context context){
		this.requester = new ServerContacter(server, context);
		this.processor = new PythonProcessor();
	}
	
	public void run(){
		done = false;
		//discard any old results from last run
		simulationResults = null;
		environmentID = null;
		segmentID = null;
		while(!done){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(!requester.onAllowedNetwork()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			RTPResponse serverResponse = requester.sendRTPPing(simulationResults, environmentID, segmentID);
			environmentID = serverResponse.getEnvironmentID();
			segmentID = serverResponse.getSegmentID();
			if(serverResponse.getResponseCode() == 200){
				String simulationPath = serverResponse.getSimulationFilePath();
				Log.v("Reponse", simulationPath);
				// TODO Create thread for processing the python script
				boolean abort = false;
				// TODO while not finished processing and abort is false
				while(!abort){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(requester.onAllowedNetwork()){
						Log.v("RTO", "Sending RTO ping");
						requester.sendRTOPing(environmentID, segmentID);
					}
					else{
						abort = true;
					}
				}
			}
		}
	}
	
	public void exit(){
		done = true;
	}

}
