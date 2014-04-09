package nl.stoux.slapbridged.objects;

import java.io.Serializable;

public class SendableContainer implements Serializable {

	private static final long serialVersionUID = -5246807622986883947L;

	//Creation time
	private long time;
	
	//Unique ID
	private String ID;
	
	//Type
	private ObjectType type;
	
	//The object
	private Object object;
	
	public SendableContainer(ObjectType type, Object object, String ID) {
		this.type = type;
		this.object = object;
		this.ID = ID;
		time = System.currentTimeMillis();
	}
	
	/**
	 * Get the type
	 * @return
	 */
	public ObjectType getType() {
		return type;
	}
	
	/**
	 * Get the send object
	 * @return
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * Get the Unique ID of this object
	 * @return the ID
	 */
	public String getID() {
		return ID;
	}
	
	/**
	 * Get the system time this was created
	 * WARNING: The system time of a different server can be different from yours.
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

}
