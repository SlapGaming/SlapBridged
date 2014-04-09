package nl.stoux.slapbridged.bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class BukkitUtil {

	private static BukkitScheduler scheduler;
	private static SlapBridged plugin;
	private static Logger logger;
	
	/**
	 * Initialze the util
	 * @param _scheduler
	 * @param bridge
	 */
	public static void initialize(SlapBridged bridge, BukkitScheduler _scheduler, Logger _logger) {
		scheduler = _scheduler;
		plugin = bridge;
		logger = _logger;
	}
	
	/**
	 * Destroy the util
	 */
	public static void destroy() {
		scheduler = null;
		plugin = null;
	}
	
	
	/**
	 * Run a runnable in sync
	 * @param runnable the runnable
	 */
	public static void runSync(BukkitRunnable runnable) {
		scheduler.runTask(plugin, runnable);
	}
	
	/**
	 * Run a runnable a-sync
	 * @param runnable
	 */
	public static void RunASync(BukkitRunnable runnable) {
		scheduler.runTaskAsynchronously(plugin, runnable);
	}
	
	/**
	 * Log an error
	 * @param error The error
	 * @param severe is a severe error
	 */
	public static void logError(String error, boolean severe) {
		Level level = (severe ? Level.SEVERE : Level.WARNING);
		logger.log(level, error);
	}
	
	/**
	 * Broadcast a message
	 * @param message the message
	 * @param header with header
	 */
	public static void broadcast(String message, boolean header) {
		if (header) {
			message = ChatColor.GOLD + "[SLAP] " + ChatColor.WHITE + message;
		}
		Bukkit.broadcastMessage(message);
	}
	
	/**
	 * Call the given event
	 * @param event the event
	 */
	public static void callEvent(Event event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	/**
	 * Check if an event is cancelled
	 * @param event The event
	 * @return is cancelled
	 */
	public static boolean isEventCancelled(Event event) {
		if (event instanceof Cancellable) {
			return ((Cancellable) event).isCancelled();
		}
		return false;
	}
	
}
