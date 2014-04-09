package nl.stoux.slapbridged.grid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.stoux.slapbridged.grid.Grid;

public class ConsoleListener implements Runnable {

	private boolean listening;
	private Grid grid;
	
	public ConsoleListener(Grid grid) {
		this.grid = grid;
		listening = true;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while ((line = reader.readLine()) != null && listening) {
				String[] split = line.split(" ");
				if (split.length > 0) {
					String command = split[0].toLowerCase();
					switch (command) {
					case "shutdown": case "quit":
						grid.shutdown();
						return;
						
					}
				}
			}
		} catch (IOException e) {
			System.out.println("ConsoleListener exception: " + e.getMessage());
		}
	}
	
	/**
	 * Set listening
	 * @param listening is listening
	 */
	public void setListening(boolean listening) {
		this.listening = listening;
	}

}
