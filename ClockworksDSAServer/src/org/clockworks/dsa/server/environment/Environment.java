package org.clockworks.dsa.server.environment;


import java.io.File;
import java.util.Arrays;
import org.clockworks.dsa.server.singletons.PingQueue;

public class Environment {
	
	private int id;
	private EnvironmentSegment[] segments;
	private File pythonScript;
	
	/**
	 * Constructor that populates ALL member variables including segments.
	 * @param parameters
	 * @param pythonScript Must be passed after applying .setReadOnly() on the file object
	 */
	public Environment(String[] parameters,File pythonScript){
		int sizeOfPingQueue = PingQueue.sharedInstance().readSize();
		// TODO a lot
	}
	
	public int getSegmentCount(){
		return segments.length;
	}
	
	public EnvironmentSegment[] getSegments(int first, int last){
		return Arrays.copyOfRange(segments, first, last);
	}
	
	public int completeSegmentWithResults(Object results, int id){
		for(int i= 0; i<segments.length; i++){
			if(segments[i].getId()==id){
				return segments[i].insertResults(results);
			}
		}
		return 400;
	}
	
	public int getId(){
		return this.id;
	}
	
	private boolean isComplete(){
		for(EnvironmentSegment segment : segments){
			if(!segment.isComplete()){
				return false;
			}
		}
		return true;
	}
	
	public Object returnAssembledResult(){
		if(!isComplete()){
			return null;
		}
		//TODO: everything
		return null;
	}
}
