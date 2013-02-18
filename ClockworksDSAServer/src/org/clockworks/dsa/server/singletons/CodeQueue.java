package org.clockworks.dsa.server.singletons;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.clockworks.dsa.server.environment.EnvironmentSegment;

/**
 *	Class wrapping a static singleton code queue object
 *	Classes accessing:
 *	- Environment
 */
class CodeQueue{
	
	private ConcurrentLinkedQueue<EnvironmentSegment> queue;
	private static CodeQueue singleton;

	/**
	 *	Singleton accessor
	 */
	public static CodeQueue sharedInstance(){
		if(singleton == null){
			singleton = new CodeQueue();
		}
		return singleton;
	}

	/**
	 *	Private constructor
	 */
	private CodeQueue(){
		queue = new ConcurrentLinkedQueue<EnvironmentSegment>();
	}

	/**
	 *	Enqueue a block of code
	 */
	public void addToQueue(EnvironmentSegment codeblock){
		queue.add(codeblock);
	}

	/**
	 * Dequeue a block of code
	 */
	public EnvironmentSegment popFromQueue(){
		return queue.poll();
	} 

}