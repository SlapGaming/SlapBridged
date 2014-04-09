package nl.stoux.slapbridged.grid.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import nl.stoux.slapbridged.objects.SendableContainer;

public class PeerOutgoingConnection implements Runnable {

	//To be send
	private ArrayList<Object> list;
	
	//Socket info
	private Socket socket;
	private ObjectOutputStream objectSender;
	private boolean running;
	
	//Other info
	private boolean error;
	private Peer peer;
		
	public PeerOutgoingConnection(Peer peer, Socket socket) {
		this.socket = socket;
		this.peer = peer;
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
		
		synchronized (peer) {
			peer.notify();
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
				
				System.out.println("Sending a " + ((SendableContainer) target).getType() + " to " + (peer.getThisServer() == null ? "New server" : peer.getThisServer().getName()));
				
				try {
					synchronized(objectSender) {
						objectSender.writeObject(target);
						objectSender.flush();
					}
				} catch (Exception e) {
					if (peer.isConnected()) {
						peer.stopPeer();
					}
				}
			}
			
			try {
				synchronized (this) {
					this.wait(); //Wait till notify
				}
			} catch (InterruptedException e) { }
		}
	}
	
	/**
	 * Send an object to the peer
	 * @param o The object
	 */
	public void send(Object o) {
		synchronized (list) {
			list.add(o);
			synchronized (this) {
				this.notify();
			}
		}
	}
	
	/**
	 * Directly send an object to the peer
	 * @param o the object
	 */
	public boolean directSend(Object o) {
		try {
			synchronized(objectSender) {
				objectSender.writeObject(o);
				objectSender.flush();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
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
	 * @param running is running
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
		
	/**
	 * Try to close the connection
	 */
	public void close() {
		try {
			objectSender.close();
		} catch (IOException e) {}
	}

}
