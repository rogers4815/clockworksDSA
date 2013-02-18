package org.clockworks.dsa.server.environment;

import java.io.File;

public class EnvironmentSegment {
	
	private String[] parameters;
	private File pythonScript;
	private int parentId;
	private int id;
	private Object results;
	
	public EnvironmentSegment(int parentId, int id, String[] parameters, File pythonScript) {
		this.parentId = parentId;
		this.id = id;
		this.parameters = parameters;
		this.pythonScript = pythonScript;
	}
	
	public Object getUnifiedFile(){
		return null;
	}
	
	public int insertResults(Object results){
		if(this.results!=null){
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
