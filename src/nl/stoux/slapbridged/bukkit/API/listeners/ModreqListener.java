package nl.stoux.slapbridged.bukkit.API.listeners;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.SlapBridged;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.Modreq;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.nyancraft.reportrts.event.ReportCreateEvent;

public class ModreqListener implements Listener {

	private SlapBridged slapBridge;
	
	public ModreqListener() {
		this.slapBridge = SlapBridged.getInstance();
	}
	
	@EventHandler
	public void onNewModreq(ReportCreateEvent event) {
		//Get bridge & check if connected
		Bridge bridge = slapBridge.getBridge();
		if (!bridge.isConnected()) return;
		
		//Create & send conatiner
		SendableContainer container = new SendableContainer(
			ObjectType.NEW_MODREQ,
			new Modreq(bridge.getThisServer().getName(), event.getRequest().getName(), event.getRequest().getMessage()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}

}
