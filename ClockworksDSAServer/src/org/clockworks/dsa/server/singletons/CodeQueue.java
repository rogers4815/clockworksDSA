package org.clockworks.dsa.server.singletons;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *	Class wrapping a static singleton code queue object
 */
class CodeQueue{
	
	private ConcurrentLinkedQueue<CodeBlock> queue;
	private static CodeQueue singleton;

	/**
	 *	Static constructor
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
		queue = new ConcurrentLinkedQueue<CodeBlock>();
	}

	/**
	 *	Enqueue a block of code
	 */
	public void addToQueue(CodeBlock codeblock){
		queue.add(codeblock);
	}

	/**
	 * Dequeue a block of code
	 */
	public CodeBlock popFromQueue(){
		return queue.pull();
	} 

}