package nl.stoux.slapbridged.grid.service;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.grid.Grid;
import nl.stoux.slapbridged.grid.connection.Peer;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.Ping;

public class PingService implements Runnable {

	//Singleton
	private static PingService instance;
	
	//Map containing pings
	private ConcurrentHashMap<String, Ping> idToPing;
	private ConcurrentHashMap<String, Ping> serverToPing;
	
	//Info
	private boolean pinging;
	private Grid grid;
	
	public PingService(Grid grid) {
		instance = this;
		
		this.grid = grid;
		idToPing = new ConcurrentHashMap<>();
		serverToPing = new ConcurrentHashMap<>();
		pinging = true;
	}
	
	@Override
	public void run() {
		while (pinging) {
			HashSet<Peer> servers = new HashSet<Peer>(grid.getServers().values());
			for (Peer server : servers) {
				if (System.currentTimeMillis() - server.getLastContact() > 15000) { //If last contact was more than 15 seconds ago
					//Check if there are any standing pings
					if (serverToPing.containsKey(server.getThisServer().getName())) {
						Ping ping = serverToPing.get(server.getThisServer().getName()); //Get the Ping
						if (System.currentTimeMillis() - ping.getSend() > 5000) { //check if longer than 5 seconds ago
							//Assuming connection has been lost
							serverToPing.remove(server.getThisServer().getName());
							idToPing.remove(ping.getCallbackID());
							if (server.isConnected()) {
								GridThreadPool.run(new PingDisconnecter(server));
							}
						} //Else wait till that time has passed
					} else { //No pings found => Ping the server
						//Create a new ping
						Ping p = new Ping(IdentifierGenerator.nextID());
						//	=> Put it in the maps
						idToPing.put(p.getCallbackID(), p);
						serverToPing.put(server.getThisServer().getName(), p);
						//	=> Create & Send container
						SendableContainer container = new SendableContainer(
							ObjectType.PING,
							p,
							p.getCallbackID()
						);
						server.send(container);
					}
				}
			}
			try {
				synchronized(this) {
					wait(2500); //Wait a bit for checking again
				}
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * Set pinging
	 * @param pinging
	 */
	public void setPinging(boolean pinging) {
		this.pinging = pinging;
	}
		
	/**
	 * Recieved a ping
	 * @param ID the id of the ping
	 */
	public static void recievedPing(String server, String ID) {
		if (instance == null) return; //Shouldn't be possible, as this service is sending the pings
		instance.serverToPing.remove(server);
		instance.idToPing.remove(ID);
	}	
	
	private class PingDisconnecter implements Runnable {
		
		private Peer peer;
		public PingDisconnecter(Peer peer) {
			this.peer = peer;
		}
		
		@Override
		public void run() {
			peer.stopPeer();
		}
	}
	
}
