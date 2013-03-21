package org.clockworks.dsa.application;

import java.io.FileOutputStream;
import java.io.IOException;

public class RTPResponse {
	private String filePath = "simulation.py";
	private int responseCode;
	
	public RTPResponse(int responseCode, byte[] response){
		if(response != null){
			try {
				FileOutputStream file = new FileOutputStream(this.filePath);
				file.write(response);
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
}
