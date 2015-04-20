package jpmorgan.hlevnjak.messagingsystem;

import java.util.concurrent.CountDownLatch;

/***
 * The class Resource represents a resource, which holds the responsibility to process 
 * messages to be sent to the Gateway. 
 * @author mhlevnjak
 *
 */
public abstract class Resource {

	protected String name;
	
	public Resource(String name) {
		this.name = name;
	}
	
	/**
	 * Starts the resource, changes status to not available. Not supposed to be called
	 * outside of a ResourceManager object
	 * @throws InterruptedException 
	 */
	public abstract void start(Message msg, Gateway gateway, CountDownLatch resourceEnds) throws InterruptedException;
	
	/**
	 * Returns an integer representing the speed of the resource
	 * @return
	 */
	public abstract int getProcessingSpeed();
	
	/**
	 * @return the availability of the resource
	 */
	public abstract boolean isAvailable();

	/**
	 * @return the name of the resource
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
