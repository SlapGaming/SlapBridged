package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Chat extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = -2878550388660548368L;
	
	private String chatMessage;
	
	public Chat(String server, String player, String chatMessage) {
		super(server, player);
		this.chatMessage = chatMessage;
	}

	/**
	 * Get the chat message this player typed, no ChatColor translation has been done
	 * @return the message
	 */
	public String getChatMessage() {
		return chatMessage;
	}
	

}
