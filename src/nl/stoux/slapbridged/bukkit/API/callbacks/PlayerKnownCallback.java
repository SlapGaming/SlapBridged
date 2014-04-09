package nl.stoux.slapbridged.bukkit.API.callbacks;

import nl.stoux.slapbridged.objects.sendables.KnownPlayerRequest;

public class PlayerKnownCallback implements Callback {

	private KnownPlayerRequest request;
	
	/**
	 * Set the request
	 * @param request
	 */
	public void setRequest(KnownPlayerRequest request) {
		this.request = request;
	}
	
	/**
	 * Get the request
	 * @return the request
	 */
	public KnownPlayerRequest getRequest() {
		return request;
	}
	
	@Override
	public void Action() {
		//TODO: Actions
	}
	
}
