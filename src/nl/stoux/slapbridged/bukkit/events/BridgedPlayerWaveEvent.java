package nl.stoux.slapbridged.bukkit.events;

import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

public class BridgedPlayerWaveEvent extends AbstractEvent {

	private OtherPlayer wavingPlayer;
	private OtherPlayer wavedToPlayer;
	
	private boolean waveToEveryone;
	
	public BridgedPlayerWaveEvent(OtherServer server, long eventTime, OtherPlayer fromPlayer, OtherPlayer toPlayer) {
		super(server, eventTime);
		this.wavingPlayer = fromPlayer;
		this.wavedToPlayer = toPlayer;
		waveToEveryone = false;
	}
	
	public BridgedPlayerWaveEvent(OtherServer server, long eventTime, OtherPlayer fromPlayer) {
		super(server, eventTime);
		waveToEveryone = true;
	}
	
	/**
	 * Is waving to everyone
	 * @return is waving to everyone
	 */
	public boolean isWaveToEveryone() {
		return waveToEveryone;
	}
	
	/**
	 * Get the player who gets waved to
	 * @return the player
	 */
	public OtherPlayer getWavedToPlayer() {
		return wavedToPlayer;
	}
	
	/**
	 * Get the player who is waving
	 * @return the player
	 */
	public OtherPlayer getWavingPlayer() {
		return wavingPlayer;
	}

}
