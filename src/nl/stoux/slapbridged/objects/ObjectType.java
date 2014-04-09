package nl.stoux.slapbridged.objects;

public enum ObjectType {

	//Grid communcation
	GRID_JOIN, //=> Joining the Grid (Send by Server)
	GRID_WELCOME, //=> Grid acknowledging the new server (Send by Grid)
	GRID_GOODBYE, //=> Server leaving the grid (Send by Server)
	GRID_SHUTDOWN, //=> Grid shutting down (Send by Grid)
	
	//Events
	PLAYER_AFK,
	PLAYER_LEAVE_AFK,
	PLAYER_CHAT,
	PLAYER_JOIN,
	PLAYER_QUIT,
	SERVER_CONNECT,
	SERVER_DISCONNECT,
	NEW_MODREQ,
	CHAT_CHANNEL_MESSAGE,
	
	//Requests
	KNOWN_PLAYER_REQUEST,
	KNOWN_PLAYER_RESPONSE,
	
	
	//Others
	PING; //=> The Grid pining the server
	
}
