package nl.stoux.slapbridged.grid.service;

import java.util.HashSet;

import nl.stoux.slapbridged.grid.Grid;
import nl.stoux.slapbridged.grid.callback.GridCallback;

public class CallbackService implements Runnable {

	//The grid
	private Grid grid;
	
	//Is running
	private boolean running;
	
	public CallbackService(Grid grid) {
		this.grid = grid;
		running = true;
	}
	
	@Override
	public void run() {
		while(running) {
			if (!grid.getCallbacks().isEmpty()) { //If any callbacks present
				HashSet<GridCallback> callbacks = new HashSet<GridCallback>(grid.getCallbacks().values()); //Create new set
				for (GridCallback callback : callbacks) { //Loop thru callbacks
					if (System.currentTimeMillis() - callback.getCreated() > callback.getTimeout()) { //Check if the callback timedout, if so => Finish it
						callback.finish();
					}
				}
			}
			
			try {
				synchronized(this) {
					wait(2500); //Wait for 2.5 seconds
				}
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Set the running status
	 * @param running status
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

}
