package nl.stoux.slapbridged.grid.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import nl.stoux.slapbridged.grid.Grid;
import nl.stoux.slapbridged.grid.service.GridThreadPool;
import nl.stoux.slapbridged.grid.service.PingService;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;

public class PeerIncomingConnection implements Runnable {

	//Other info
	private boolean error;
	
	//The grid & peer
	private Grid grid;
	private Peer peer;
	
	//The socket
	private Socket socket;
	private ObjectInputStream objectReciever;
	private boolean running;
	
	public PeerIncomingConnection(Grid grid, Peer peer, Socket socket) {
		this.socket = socket;
		this.grid = grid;
		this.peer = peer;
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
		
		synchronized (peer) {
			peer.notify();
		}
		if (error) return; //Return if has error
		
		//Recieving
		Object o;
		try {
			while((o = objectReciever.readObject()) != null && running) {
				if (!(o instanceof SendableContainer)) {
					continue; //Discard any object's that arent a sendable one
				}
								
				//Just received something => Set new last ping
				peer.setLastContact(System.currentTimeMillis());
				
				SendableContainer recieved = (SendableContainer) o;
				System.out.println("Recieved a " + recieved.getType() + " from " + (peer.getThisServer() == null ? "New server" : peer.getThisServer().getName()));
				if (recieved.getType() == ObjectType.PING) { //Recieved a ping return
					PingService.recievedPing(peer.getThisServer().getName(), recieved.getID()); //Let PingService know that a response has been recieved
				} else {
					GridThreadPool.run(new PeerIncomingHandler(grid, peer, recieved)); //Let a seperate thread deal with it
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Did not find the class: " + e.getMessage()); //Shouldn't be reached
		} catch (IOException e) {
			if (peer.isConnected()) {
				peer.stopPeer();
			}
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
	 * Set the running status
	 * @param running is running
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
	
	/**
	 * Try to close the connection
	 */
	public void close() {
		try {
			objectReciever.close();
		} catch (IOException e) {}
	}

}
