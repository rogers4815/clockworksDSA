package org.clockworks.dsa.application;

public class RTPResponse {
	private String[] response;
	private String filePath;
	private int responseCode;
	
	public RTPResponse(int responseCode, byte[] response){
		if(response != null){
			this.response = (new String(response)).split("\n");
		}
		this.responseCode = responseCode;
	}

	public String getSimulationFilePath() {
		return filePath;
	}
	
	public int getResponseCode(){
		return responseCode;
	}
}
