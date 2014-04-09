package nl.stoux.slapbridged.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import nl.stoux.slapbridged.bukkit.BukkitUtil;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;

import org.bukkit.scheduler.BukkitRunnable;

public class IncomingConnection extends BukkitRunnable {

	//Other info
	private boolean error;
	
	//The bridge
	private Bridge bridge;
	
	//The socket
	private Socket socket;
	private ObjectInputStream objectReciever;
	private boolean running;
	
	public IncomingConnection(Bridge bridge, Socket socket) {
		this.socket = socket;
		this.bridge = bridge;
		running = false;
		error = false;
	}

	@Override
	public void run() {
		//Create incoming con
		try {
			objectReciever = new ObjectInputStream(socket.getInputStream());
			running = true;
		} catch (IOException e) {
			running = false;
			error = true;
		}
		
		synchronized(bridge) {
			bridge.notify(); //Notify the bridge
		}
		if (error) return; //Return if has error
		
		//Recieving
		Object o;
		try {
			while((o = objectReciever.readObject()) != null && running) {
				if (!(o instanceof SendableContainer)) {
					continue; //Discard any object's that arent a sendable one
				}
				
				SendableContainer recieved = (SendableContainer) o;
				if (recieved.getType() == ObjectType.PING) { //If just another ping event
					bridge.getOutgoing().send(recieved); //Instantly return it
				} else {
					BukkitUtil.RunASync(new IncomingObjectHandler(bridge, recieved)); //Run in a-sync to handle it
				}
			}
		} catch (ClassNotFoundException e) {
			BukkitUtil.logError("Did not find the class: " + e.getMessage(), true);
		} catch (IOException e) {
			if (bridge.isConnected()) bridge.shutdown(true);
		}
	}
	
	/**
	 * Is running
	 * @return running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Set is running
	 * @param running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * See if the connection has an error
	 * @return has error
	 */
	public boolean hasError() {
		return error;
	}

}
