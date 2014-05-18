package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 8884541482192286743L;

	private String fromServer;
	private String fromPlayer;
	private String toPlayer;
	private String message;
	private boolean colorMessage;
	
	/**
	 * Create a new Message (/msg)
	 * @param fromServer The server where this message was created
	 * @param fromPlayer The player who send the message
	 * @param toPlayer The player who receives the message
	 * @param message The message
	 */
	public Message(String fromServer, String fromPlayer, String toPlayer, String message, boolean colorMessage) {
		this.fromServer = fromServer;
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.message = message;
		this.colorMessage = colorMessage;
	}
	
	/**
	 * Get the server where this message was created
	 * @return the server name
	 */
	public String getFromServer() {
		return fromServer;
	}
	
	/**
	 * Get the player who send the message
	 * @return the player
	 */
	public String getFromPlayer() {
		return fromPlayer;
	}
	
	/**
	 * Get the player who received the message
	 * @return the player
	 */
	public String getToPlayer() {
		return toPlayer;
	}
	
	/**
	 * The send message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * The message should be colored
	 * @return colored
	 */
	public boolean isColorMessage() {
		return colorMessage;
	}

}
