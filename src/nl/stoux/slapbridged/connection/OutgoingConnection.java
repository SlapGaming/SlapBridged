package nl.stoux.slapbridged.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import nl.stoux.slapbridged.objects.SendableContainer;

import org.bukkit.scheduler.BukkitRunnable;

public class OutgoingConnection extends BukkitRunnable {

	//To be send
	private ArrayList<Object> list;
	
	//Socket info
	private Socket socket;
	private ObjectOutputStream objectSender;
	private boolean running;
	
	//Other info
	private boolean error;
	private Bridge bridge;
		
	public OutgoingConnection(Bridge bridge, Socket socket) {
		this.socket = socket;
		this.bridge = bridge;
		list = new ArrayList<>();
		running = false;
		error = false;
	}
	
	@Override
	public void run() {
		//Create outgoing socket
		try {
			objectSender = new ObjectOutputStream(socket.getOutputStream());
			running = true;
		} catch (IOException e) {
			running = false;
			error = true;
		}
		
		synchronized(bridge) {
			bridge.notify(); //Notify the bridge
		}
		if (error) return; //Return if has error
		
		//Start sending stuff
		while (running) {
			while (true) {
				Object target = null;
				synchronized (list) {
					if (list.size() > 0) { //If has first
						target = list.get(0);
						list.remove(0);
					}
				}
				
				if (target == null) { //No items left => Break this loop & go into waiting
					break;
				}
				
				try {
					synchronized(objectSender) {
						objectSender.writeObject(target); //Write object
						objectSender.flush(); //Flush to server
					}
				} catch (IOException e) {
					if (bridge.isConnected()) bridge.shutdown(true);
				}
			}
			
			try {
				synchronized(this) {
					wait(); //Wait till notify
				}
			} catch (InterruptedException e) { }
		}
		
		try {
			objectSender.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Send an object to the grid
	 * @param o The object
	 */
	public void send(Object o) {
		synchronized (list) {
			list.add(o);
			synchronized(this) {
				notify();
			}
		}
	}
	
	/**
	 * Direct send the container
	 * @param container
	 */
	public void directSend(SendableContainer container) {
		try {
			synchronized(objectSender) {
				objectSender.writeObject(container); //Write object
				objectSender.flush(); //Flush to server
			}
		} catch (IOException e) {}
	}
	
	/**
	 * See if this connection is running
	 * @return is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Set the running status
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * Check if this thread has an error
	 * @return has error
	 */
	public boolean hasError() {
		return error;
	}

}
