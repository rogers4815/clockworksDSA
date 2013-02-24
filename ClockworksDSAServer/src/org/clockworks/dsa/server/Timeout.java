package org.clockworks.dsa.server;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Generic timeout-handling class
 * Probably to be subclassed/embedded into BotRequestHandler
 */
public class Timeout{

    private Timer timer;
    private int millis;

    /**
     *	Constructor
     */
    public Timeout(int millis){
	this.millis = millis;
	timer = new Timer();
	timer.schedule(new TimeoutTask(), millis);
    }

    public int getMillis(){
	return this.millis;
    }

    /**
     *	Stop timer, preventing TimeoutTask from being executed
     */
    public void stop(){
	timer.cancel();
    }

    /**
     * Timeout task handler class
     *
     */
    public class TimeoutTask extends TimerTask{

	public void run(){

	    // TODO: Insert action to be performed when timeout has occurred

	    timer.cancel(); // Stop the current timer
	    timer = new Timer();
	    timer.schedule(new TimeoutTask(), getMillis()); // Start another timer with the same timeout interval
	}

    }

}