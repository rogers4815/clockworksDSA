package org.clockworks.dsa.server.singletons;

import java.util.LinkedList;

import org.clockworks.dsa.server.environment.Environment;

/**
 * Class wrapping the static singleton list of environments
 * Classes accessing (add as relevant):
 *  -	BotRequestHandler
 *  -	EnvironmentHandler
 *  -	ResultAssemblyHandler
 *
 */
public class EnvironmentList {

	private static EnvironmentList singleton;
	private LinkedList<Environment> list;
	
	/**
	 * Singleton accessor
	 */
	public static EnvironmentList sharedInstance(){
		if(singleton==null){
			singleton = new EnvironmentList();
		}
		return singleton;
	}
	
	/**
	 * Private constructor
	 */
	private EnvironmentList() {
		list = new LinkedList<Environment>();
	}
	
	/**
	 * Assorted synchronised public accessor methods
	 */
	
	synchronized public Environment getEnvironmentById(int id){
		for(Environment e : list){
			if(e.getId()==id){
				return e;
			}
		}
		return null;
	}
	
	synchronized public boolean addEnvironment(Environment e){
		if(getEnvironmentById(e.getId())!=null){
			return false;
		}else{
			list.add(e);
			return true;
		}
	}
	
	synchronized public boolean deleteEnvironment(Environment e){
		return list.remove(e);
	}

}
