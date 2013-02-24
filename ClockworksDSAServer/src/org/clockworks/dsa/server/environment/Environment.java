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
	
	/**
	 * Return the number of segments this simulation has been split up into
	 * @return
	 */
	public int getSegmentCount(){
		return segments.length;
	}
	
	/**
	 * Return a sub-array of the segments within a certain range
	 * @param first
	 * @param last
	 * @return
	 */
	public EnvironmentSegment[] getSegments(int first, int last){
		return Arrays.copyOfRange(segments, first, last);
	}
	
	/**
	 * Attempt to associate a particular segment of the simulation with its results
	 * @param results
	 * @param id
	 * @return
	 */
	public int completeSegmentWithResults(Object results, int id){
		for(int i= 0; i<segments.length; i++){
			if(segments[i].getId()==id){
				return segments[i].insertResults(results);
			}
		}
		return 400; // Out of range, some sort of error
	}
	
	public int getId(){
		return this.id;
	}
	
	/**
	 * Return true if all the segments of this simulation have results associated
	 * @return
	 */
	private boolean isComplete(){
		for(EnvironmentSegment segment : segments){
			if(!segment.isComplete()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return a single object consisting of the the results from all the simulation
	 * segments combined
	 * @return
	 */
	public Object returnAssembledResult(){
		if(!isComplete()){
			return null;
		}
		//TODO: everything
		return null;
	}
}
