package nl.stoux.slapbridged.grid;

import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.grid.callback.GridCallback;
import nl.stoux.slapbridged.grid.connection.GridServer;
import nl.stoux.slapbridged.grid.connection.Peer;
import nl.stoux.slapbridged.grid.service.CallbackService;
import nl.stoux.slapbridged.grid.service.ConsoleListener;
import nl.stoux.slapbridged.grid.service.GridThreadPool;
import nl.stoux.slapbridged.grid.service.PingService;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;

public class Grid {

	//Server
	private GridServer server;
	
	//Services
	private CallbackService callbackService;
	private PingService pingService; 
	private ConsoleListener consoleListener;
	
	//Maps
	private ConcurrentHashMap<String, GridCallback> callbacks;
	private ConcurrentHashMap<String, Peer> servers;
		
	public Grid() {
		callbacks = new ConcurrentHashMap<>();
		servers = new ConcurrentHashMap<>();
		
		//Create server
		new Thread(server = new GridServer(this)).start();
		
		//Start waiting for the server to start up
		while(true) {
			if (server.hasError()) { //Failed to setup
				System.out.println("Error! Stopping the Grid.");
				return;
			}
			
			if (server.isAccepting()) { //If accepting, break, server is working fine.
				break;
			}
			
			try {
				synchronized(this) {
					wait(); //Wait till waked up by the server thread
				}
			} catch (InterruptedException e) {}
		}
		
		//=> Create pools & services
		GridThreadPool.initialize();
		new Thread(callbackService = new CallbackService(this)).start();
		new Thread(pingService = new PingService(this)).start();
		new Thread(consoleListener = new ConsoleListener(this)).start();
		
		//Fully setup
		System.out.println("Grid running!");
	}
	
	/**
	 * Shutdown the Grid
	 */
	public void shutdown() {
		//=> Close the accepting server
		server.shutdown();
		
		//=> Stop services
		callbackService.setRunning(false);
		synchronized (callbackService) {
			callbackService.notify();
		}
		pingService.setPinging(false);
		synchronized (pingService) {
			pingService.notify();
		}
		consoleListener.setListening(false);
				
		//=> Create container
		SendableContainer shutdownContainer = new SendableContainer(
			 ObjectType.GRID_SHUTDOWN,
			 null,
			 IdentifierGenerator.nextID()
		);

		//=> Stop peers
		for (Peer peer : servers.values()) {
			peer.stopPeer(shutdownContainer);
		}
		
		//Kill the thread pool
		GridThreadPool.destroy();
		//Destroy the ID generator
		IdentifierGenerator.destroy();
	}
	
	
	/**
	 * Get servers
	 * K: [Server name] => V: [The Peer]
	 * @return server map
	 */
	public ConcurrentHashMap<String, Peer> getServers() {
		return servers;
	}
	
	/**
	 * Get callbacks
	 * K: [ID] => V: [Callback]
	 * @return callbacks map
	 */
	public ConcurrentHashMap<String, GridCallback> getCallbacks() {
		return callbacks;
	}
	
	/**
	 * Send a container to the whole grid excluding one server
	 * @param container The container
	 * @param exclude The name of the excluded server
	 */
	public void sendToGrid(SendableContainer container, String exclude) {
		for (Peer peer : servers.values()) {
			if (peer.getThisServer().getName().equals(exclude)) { //Skip the server if it is excluded
				continue;
			}
			peer.send(container);
		}
	}

}
