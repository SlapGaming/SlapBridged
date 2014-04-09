package nl.stoux.slapbridged;

import java.math.BigInteger;
import java.security.SecureRandom;

public class IdentifierGenerator {

	//Random generator
	private static SecureRandom random = new SecureRandom();
	
	/**
	 * Destroy the generator
	 */
	public static void destroy() {
		random = null;
	}
	
	/**
	 * Create a new random ID
	 * @return the ID
	 */
	public static String nextID() {
		return new BigInteger(130, random).toString(32);
	}

}
