package nl.stoux.slapbridged.bukkit;

import java.util.concurrent.ConcurrentHashMap;

import nl.stoux.slapbridged.IdentifierGenerator;
import nl.stoux.slapbridged.bukkit.API.BridgeAPI;
import nl.stoux.slapbridged.bukkit.API.listeners.ModreqListener;
import nl.stoux.slapbridged.bukkit.API.listeners.PlayerListener;
import nl.stoux.slapbridged.connection.Bridge;
import nl.stoux.slapbridged.objects.OtherPlayer;
import nl.stoux.slapbridged.objects.OtherServer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nyancraft.reportrts.ReportRTS;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class SlapBridged extends JavaPlugin {

	//Singleton
	private static SlapBridged instance;
	
	/**
	 * Get the instance of this plugin
	 * @return the instance
	 */
	public static SlapBridged getInstance() {
		return instance;
	}
	
	//API
	private static BridgeAPI api;
	
	/**
	 * Get the Bridge API
	 * @return the API interface
	 */
	public static BridgeAPI getAPI() {
		return api;
	}
	
	
	//Attributes
	private Bridge bridge; //The bridge
	
	
	@Override
	public void onEnable() {
		//Set statics
		instance = this;
		BukkitUtil.initialize(this, getServer().getScheduler(), getLogger());
		
		//Get information about this server
		//	=> IP & Port
		String IP = getServer().getIp();
		int port = getServer().getPort();
		
		FileConfiguration config = this.getConfig();
		//	=> General info
		String name = config.getString("servername");
		String tabname = config.getString("servertabname");
		String chatPrefix = config.getString("serverchatprefix");
		
		//	=> Create server object
		OtherServer thisServer = new OtherServer(IP, port, name, tabname, chatPrefix, null);
		
		//	=> Get players
		ConcurrentHashMap<String, OtherPlayer> playerMap = new ConcurrentHashMap<>();
		for (Player p : getServer().getOnlinePlayers()) {
			PermissionUser user = PermissionsEx.getUser(p.getName()); //Get PexUser
			OtherPlayer oPlayer = new OtherPlayer( //Create new player
				p.getName(),
				user.getGroups()[0].getRank(), 
				user.getPrefix()
			);
			playerMap.put(p.getName(), oPlayer); //Put in map
		}
		thisServer.setPlayers(playerMap);
		
		//Get grid information
		String gridIP = config.getString("gridip");
		int gridPort = config.getInt("gridport");
		
		//Connect to grid
		BukkitUtil.RunASync(bridge = new Bridge(thisServer, gridIP, gridPort));
		
		//Create API
		api = new BridgeApiImpl(bridge);
		
		//Implements listeners
		PluginManager pm = getServer().getPluginManager(); //Get PM
		pm.registerEvents(new PlayerListener(), this); //Register general player events
		
		//	=> Check if ReportRTS is enabled
		ReportRTS reportRTS = (ReportRTS) pm.getPlugin("ReportRTS");
		if (reportRTS != null && reportRTS.isEnabled()) {
			pm.registerEvents(new ModreqListener(), this);
		}
		
		//	=> ServerListPing
		//Disabled due to 1.6 <=> 1.7 Problems
		//pm.registerEvents(new PingListener(config.getInt("maxplayers"), config.getStringList("messages")), this);
	}
	
	@Override
	public void onDisable() {	
		bridge.disconnect();
		
		IdentifierGenerator.destroy();
		BukkitUtil.destroy();
	}
	
	/**
	 * Get the Bridge object
	 * @return the bridge
	 */
	 public Bridge getBridge() {
		return bridge;
	}
	

}
