package org.clockworks.dsa.server.environment;

import java.io.File;

public class EnvironmentSegment {
	
	private String[] parameters;
	private File pythonScript;
	private int parentId;
	private int id;
	private Object results;
	
	/**
	 * Constructor
	 * @param parentId
	 * @param id
	 * @param parameters
	 * @param pythonScript
	 */
	public EnvironmentSegment(int parentId, int id, String[] parameters, File pythonScript) {
		this.parentId = parentId;
		this.id = id;
		this.parameters = parameters;
		this.pythonScript = pythonScript;
	}
	
	/**
	 * Create a single file/object consisting of the python script together with
	 * the parameters relevant to this segment
	 * @return
	 */
	public Object getUnifiedFile(){
		return null;
	}
	
	/**
	 * Associate result object with this segment of the simulation
	 * @param results
	 * @return
	 */
	public int insertResults(Object results){
		if(this.results!=null){ // Where a duplicate has been received
			return 409;
		}else{
			this.results = results;
			return 200;
		}
	}
	
	public boolean isComplete(){
		return this.results!=null;
	}
	
	public Object getResults(){
		return this.results;
	}
	
	public int getId(){
		return this.id;
	}

}
