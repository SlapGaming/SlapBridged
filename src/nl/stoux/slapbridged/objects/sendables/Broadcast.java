package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

import org.bukkit.ChatColor;

public class Broadcast implements Serializable {

	//Serial
	private static final long serialVersionUID = -5366171122429608444L;

	//Info
	private String message;
	private String server;
	private boolean showSendingServer;
	private boolean prependSlap;
	
	
	/**
	 * A broadcast object
	 * @param server The sending server
	 * @param message The message
	 * @param showSendingServer The other servers should prepend [Name of server] to the broadcast
	 * @param prependSlap The other servers should prepend [SLAP] to the broadcast (will be after sending server)
	 */
	public Broadcast(String server, String message, boolean showSendingServer, boolean prependSlap) {
		this.server = server;
		this.message = message;
		this.showSendingServer = showSendingServer;
		this.prependSlap = prependSlap;
	}
	
	/**
	 * Get the color coded message
	 * @return the message
	 */
	public String getColorCodedMessage() {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	/**
	 * Get the plain message
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Get the Server name
	 * @return the name
	 */
	public String getServer() {
		return server;
	}
	
	/**
	 * Receiving server should show [Sending server] prepend
	 * @return should show
	 */
	public boolean isShowSendingServer() {
		return showSendingServer;
	}
	
	/**
	 * Prepend [SLAP] to the broadcast
	 * @return prepend
	 */
	public boolean isPrependSlap() {
		return prependSlap;
	}
	

}
