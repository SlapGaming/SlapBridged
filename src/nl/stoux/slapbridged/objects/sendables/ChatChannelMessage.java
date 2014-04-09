package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class ChatChannelMessage extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = -173846278804696419L;
	
	private String chatChannel;
	private String message;
	
	public ChatChannelMessage(String server, String player, String chatChannel, String message) {
		super(server, player);
		this.chatChannel = chatChannel;
		this.message = message;
	}

	/**
	 * Get the chat channel this message was said in
	 * @return the channel
	 */
	public String getChatChannel() {
		return chatChannel;
	}
	
	/**
	 * Get the message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
