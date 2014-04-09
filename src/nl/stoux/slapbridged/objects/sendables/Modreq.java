package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class Modreq extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = 4405682746576554827L;

	private String modreq;
	
	public Modreq(String server, String player, String modreq) {
		super(server, player);
		this.modreq = modreq;
	}
	
	/**
	 * Get the modreq message
	 * @return the modreq
	 */
	public String getModreq() {
		return modreq;
	}

}
