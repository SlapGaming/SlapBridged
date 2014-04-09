package nl.stoux.slapbridged.grid.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GridThreadPool {

	private static ExecutorService service;
		
	/**
	 * Initialize the pool
	 */
	public static void initialize() {
		service = Executors.newFixedThreadPool(10); //Create 10 threads
	}
	
	public static void destroy() {
		service.shutdown();
		service = null;
	}
	
	/**
	 * Run a runnable
	 * @param runnable
	 */
	public static void run(Runnable runnable) {
		service.execute(runnable);
	}

}
