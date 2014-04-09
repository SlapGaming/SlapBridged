package nl.stoux.slapbridged.grid.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import nl.stoux.slapbridged.grid.Grid;

public class GridServer implements Runnable {

	//The Grid
	private Grid grid;
	private ServerSocket socket;
	
	//Connection status
	private boolean accepting;
	private boolean error;
	
	public GridServer(Grid grid) {
		this.grid = grid;
		accepting = true;
		error = false;
	}
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(28755); //Create socket
			
			//=> Socket creation success, notify grid
			synchronized(grid) {
				grid.notify();
			}
			
			//Start listening to incoming connections
			Socket incoming;
			while ((incoming = socket.accept()) != null && accepting) { //On an incoming connection
				//=> Create a new thread for that peer
				System.out.println("Accepting new Socket! IP: " + incoming.getInetAddress().getHostAddress());
				new Thread(new Peer(grid, incoming)).start();
			}
			socket.close();
		} catch (SocketException e) { //Socket got closed
			System.out.println("Socket closed. " + e.getMessage());
			error = false;
			accepting = false;
		} catch (IOException e) {
			error = true;
			System.out.println(e.getMessage());
			synchronized(grid) {
				grid.notify(); //Notify grid of failing
			}
		} 
	}
	
	/**
	 * Shutdown the server
	 */
	public void shutdown() {
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
	/**
	 * See if this server is accepting connections
	 * @return is accepting
	 */
	public boolean isAccepting() {
		return accepting;
	}
	
	/**
	 * See if an error occured
	 * @return has error
	 */
	public boolean hasError() {
		return error;
	}

}
