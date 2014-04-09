package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class KnownPlayerRequest extends ExistingPlayer implements Serializable {

	private static final long serialVersionUID = -1364152509578922312L;
	
	public boolean known;
	
	public KnownPlayerRequest(String server, String player) {
		super(server, player);
		known = false;
	}
	
	/**
	 * Set if this player is known
	 * @param known is known
	 */
	public void setKnown(boolean known) {
		this.known = known;
	}
	
	/**
	 * Check if the player is known
	 * @return is known
	 */
	public boolean isKnown() {
		return known;
	}

}
