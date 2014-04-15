package nl.stoux.slapbridged.bukkit.listeners;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.SlapBridged;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.ObjectType;
import nl.stoux.slapbridged.objects.SendableContainer;
import nl.stoux.slapbridged.objects.sendables.Modreq;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.nyancraft.reportrts.data.HelpRequest;
import com.nyancraft.reportrts.event.ReportClaimEvent;
import com.nyancraft.reportrts.event.ReportCompleteEvent;
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
		
		HelpRequest request = event.getRequest();
		
		//Create & send conatiner
		SendableContainer container = new SendableContainer(
			ObjectType.NEW_MODREQ,
			new Modreq(bridge.getThisServer().getName(), request.getName(), request.getId(), request.getMessage()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}
	
	@EventHandler
	public void onClaimModreq(ReportClaimEvent event) {
		Bridge bridge = slapBridge.getBridge();
		if (!bridge.isConnected()) return;
		
		HelpRequest request = event.getRequest();
		
		//Create & send conatiner
		SendableContainer container = new SendableContainer(
			ObjectType.CLAIM_MODREQ,
			new Modreq(bridge.getThisServer().getName(), request.getName(), request.getId(), request.getMessage(), request.getModName()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}
	
	@EventHandler
	public void onFinishModreq(ReportCompleteEvent event) {
		Bridge bridge = slapBridge.getBridge();
		if (!bridge.isConnected()) return;
		
		HelpRequest request = event.getRequest();
		
		//Create & send conatiner
		SendableContainer container = new SendableContainer(
			ObjectType.DONE_MODREQ,
			new Modreq(bridge.getThisServer().getName(), request.getName(), request.getId(), request.getMessage(), request.getModName()),
			IdentifierGenerator.nextID()
		);
		bridge.getOutgoing().send(container);
	}

}
