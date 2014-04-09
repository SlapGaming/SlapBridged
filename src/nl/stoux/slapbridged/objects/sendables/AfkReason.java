package nl.stoux.slapbridged.objects.sendables;

import java.io.Serializable;

public class AfkReason extends ExistingPlayer implements Serializable {
	
	private static final long serialVersionUID = 2260137740256097194L;
	
	private String reason;
	
	public AfkReason(String server, String player, String reason) {
		super(server, player);
		this.reason = reason;
	}
	
	/**
	 * Get the AFK reason
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

}
