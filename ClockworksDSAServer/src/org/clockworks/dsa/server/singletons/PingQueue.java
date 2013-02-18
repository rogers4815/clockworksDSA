package org.clockworks.dsa.server.singletons;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.clockworks.dsa.server.pings.RTPPing;
/**
 * List of classes accessing this singleton (KEEP UP TO DATE!!!)
 * - Environment
 *
 */
public class PingQueue{
	
	private static final int DEFAULT_VALID_DURATION = 30000;
	private int validDuration;
	private ConcurrentLinkedQueue<RTPPing> queue;
	private static PingQueue singleton;

	/**
	 *	Shared instance singleton accessor
	 * 	@return singleton object
	 */
	public static PingQueue sharedInstance(){
		if(singleton==null){
			singleton = new PingQueue();
		}
		return singleton;
	}
	/**
	 *	Private Constructor to stop people mucking around
	 */
	private PingQueue(){
		queue = new ConcurrentLinkedQueue<RTPPing>();
		validDuration = DEFAULT_VALID_DURATION;
	}

	/**
	 *	Sets the time a ping is valid in milliseconds
	 * 	@param milliseconds the duration in milliseconds
	 */
	public void setValidMilliseconds(int milliseconds){
		validDuration = milliseconds;
	}

	/**
	 * 	Method to read the valid size of the queue.
	 * 	This is done by popping off until a timestamp is reached within the valid range.
	 * 	@return remaining size of queue
	 */
	public int readSize(){
		while(queue.peek().getAgeInMillis()>validDuration){
			queue.poll();
		}
		return queue.size();
	}

	/**
	 *	Add RTPPing to Queue
	 *	@param ping ping to add
	 *	@return true on success, false on full queue
	 */
	public void addToQueue(RTPPing ping){
		queue.add(ping);
	}
}