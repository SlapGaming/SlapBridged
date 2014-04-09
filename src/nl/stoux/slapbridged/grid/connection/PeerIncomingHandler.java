package nl.stoux.slapbridged.grid.connection;

import java.util.HashSet;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.grid.Grid;
import nl.stoux.slapbridged.grid.callback.KnownPlayerCallback;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.OtherServer;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.AfkReason;
import nl.stoux.slapbridged.objects.sendables.ExistingPlayer;
import nl.stoux.slapbridged.objects.sendables.KnownPlayerRequest;
import nl.stoux.slapbridged.objects.sendables.NewPlayer;

public class PeerIncomingHandler implements Runnable {
	
	//Recieved container
	private SendableContainer container;
	
	//The grid & peer
	private Grid grid;
	private Peer peer;
	
	/**
	 * Create a new Object handler
	 * @param grid The grid
	 * @param peer The peer that this object came from
	 * @param container The container containing the object
	 */
	public PeerIncomingHandler(Grid grid, Peer peer, SendableContainer container) {
		this.container = container;
		this.grid = grid;
		this.peer = peer;
	}

	@Override
	public void run() {		
		AfkReason afkReason;
		
		switch (container.getType()) {
		//Grid events
		case GRID_JOIN: //=> Server wishes to join
			OtherServer server = (OtherServer) container.getObject();
			
			//Set server in peer
			peer.setThisServer(server);
			
			//Get servers to put in the grid
			HashSet<OtherServer> servers = new HashSet<OtherServer>();
			for(Peer peer : new HashSet<Peer>(grid.getServers().values())) {
				servers.add(peer.getThisServer().copy());
			}
			//	=> Create & send welcome container
			SendableContainer welcomeContainer = new SendableContainer(
				ObjectType.GRID_WELCOME,
				servers,
				IdentifierGenerator.nextID()
			);
			peer.send(welcomeContainer);
			
			//Add to grid
			grid.getServers().put(server.getName(), peer);
			
			//Send join message to grid
			OtherServer serverCopy = server.copy();
			//	=> Create join container for rest of the grid
			SendableContainer joinContainer = new SendableContainer(
				ObjectType.SERVER_CONNECT,
				serverCopy,
				IdentifierGenerator.nextID()
			);
			//	=> Send to grid
			grid.sendToGrid(joinContainer, server.getName());
			break;
			
		case GRID_GOODBYE: //=> Server disconnects
			//Set connected
			peer.getThisServer().setConnected(false);
			peer.setConnected(false);
			
			//Remove from grid
			grid.getServers().remove(peer.getThisServer().getName());
			
			//Create & Send disconnect container
			SendableContainer disconnectContainer = new SendableContainer(
				ObjectType.SERVER_DISCONNECT,
				peer.getThisServer().getName(),
				IdentifierGenerator.nextID()
			);
			grid.sendToGrid(disconnectContainer, peer.getThisServer().getName());
			break;
			
		//player events
		case PLAYER_AFK: //=> A bridged player goes AFK
			afkReason = (AfkReason) container.getObject();
			
			//Go AFK
			peer.getThisServer().getPlayers().get(afkReason.getPlayer()).goesAfk(afkReason.getReason());
			
			//Redirect container
			redirect();
			break;
			
		case PLAYER_LEAVE_AFK: //=> A bridged player leaves AFK
			afkReason = (AfkReason) container.getObject();
			
			//Let player leave AFK
			peer.getThisServer().getPlayers().get(afkReason.getPlayer()).leaveAFK();
			
			//Redirect container
			redirect();
			break;
						
		case PLAYER_JOIN: //=> A bridged player joins
			NewPlayer newPlayer = (NewPlayer) container.getObject();
			
			//Add player
			peer.getThisServer().playerJoins(newPlayer.getPlayer());
			
			//Redirect container
			redirect();
			break;
			
		case PLAYER_QUIT:
			ExistingPlayer quitingPlayer = (ExistingPlayer) container.getObject();
			
			//Remove player
			peer.getThisServer().playerQuits(quitingPlayer.getPlayer());
			
			//Redirect container
			redirect();
			break;
			
		//Requests
		case KNOWN_PLAYER_REQUEST:		
			KnownPlayerRequest request = (KnownPlayerRequest) container.getObject();
			
			//Check if any other servers to ask
			if (grid.getServers().size() == 1) { //Only this server
				peer.send(container); //Instantly return it
				break;
			}
			
			//	=> Asking new question
			KnownPlayerCallback callback = new KnownPlayerCallback(
				container.getID(),
				peer,
				this,
				grid.getServers().size() - 1
			);
			
			//	=> Add to callback map
			grid.getCallbacks().put(container.getID(), callback);
			
			//Redirect the question
			redirect();
			
			try {
				synchronized(this) {
					wait(); //Wait for the callback
				}
			} catch (InterruptedException e) {}
			
			//Respond
			request.setKnown(callback.isKnown());
			//	=> Return container
			peer.send(container);			
			break;
			
		case KNOWN_PLAYER_RESPONSE: //=> Response to a known player request
			Boolean known = (Boolean) container.getObject();
			try {
				((KnownPlayerCallback) grid.getCallbacks().get(container.getID())).answer(known); //Awnser the question
			} catch (Exception e) {}
			break;
		
		case PING: case GRID_WELCOME: //Shouldn't be reached
			return;
			
		//Default => Redirect container to the rest of the grid 
		case PLAYER_CHAT: case CHAT_CHANNEL_MESSAGE: case NEW_MODREQ:
		default:
			redirect();
			break;
		}

	}
	
	/**
	 * Redirect the container to the rest of the Grid
	 */
	private void redirect() {
		grid.sendToGrid(container, peer.getThisServer().getName());
	}
	
}
