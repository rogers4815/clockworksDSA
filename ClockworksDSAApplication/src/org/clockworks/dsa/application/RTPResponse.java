package org.clockworks.dsa.application;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class RTPResponse {
	private Context context;
	private String filePath, environmentID, segmentID;
	private int responseCode;
	
	public RTPResponse(int responseCode, byte[] response, String environmentID, String segmentID, Context context){
		if(response != null){
			try {
				this.context = context;
				this.filePath = context.getFilesDir().getPath().toString() + "/simulation.py";
				this.environmentID = environmentID;
				this.segmentID = segmentID;
				FileOutputStream file = new FileOutputStream(this.filePath);
				file.write(response);
				file.flush();
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.responseCode = responseCode;
	}

	public String getSimulationFilePath() {
		return this.filePath;
	}
	
	public int getResponseCode(){
		return this.responseCode;
	}
	
	public String getEnvironmentID(){
		return this.environmentID;
	}
	
	public String getSegmentID(){
		return this.segmentID;
	}
}
