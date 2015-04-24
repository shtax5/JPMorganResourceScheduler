package jpmorgan.hlevnjak.msgsys;

import java.util.ArrayList;
import java.util.List;

/***
 * ResourceManager has the responsibility to manage resources. It supports operations such as 
 * add/remove resources or start a resource.
 * @author mhlevnjak
 *
 */
public abstract class ResourceManager {
	protected List<Resource> resourceList;
	protected volatile boolean hasAvailableResources = false;
	
	public ResourceManager() {
		resourceList = new ArrayList<Resource>();
	}
	
	public ResourceManager(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}
	
	/***
	 * Called by the user to provide resources to the resource manager.
	 * @param name
	 */
	public abstract void addResource(String name);
	
	/***
	 * Receives a message, and tries to process it with any available resource at the time.
	 * @param msg - The message that needs processing
	 * @throws InterruptedException 
	 */
	public abstract void processMessage(Message msg, Gateway gateway) throws InterruptedException;
	
	/***
	 * Receives a message, and tries to process it with the given resource. The resource does not 
	 * need to belong to the resource manager
	 * @param res - The message that needs processing
	 * @param msg - The given resource
	 * @throws InterruptedException
	 */
	public abstract void processMessage(Resource res, Message msg, Gateway gateway) throws InterruptedException;
	
	/***
	 * Removes a resource from the resource manager.
	 * @param res - The resource to remove
	 * @return The result of the remove process
	 */
	public boolean removeResource(Resource res) {
		return resourceList.remove(res);
	}
	
	/***
	 * Returns true if there is at least one idle resource. A resource is idle when it is not doing 
	 * any processing.
	 * @return
	 */
	public boolean hasAvailableResources() {
		return hasAvailableResources;
	}
	
	/***
	 * Updates the hasAvailableResources boolean value. Assigns it to true if there is 
	 * at least 1 resource available. If there is at least 1 available resource, it will notify 
	 * the thread who called waitForResources to resume.
	 */
	public synchronized void updateAvailableResource() {
		if (getAvailableResources().size() == 0)
			hasAvailableResources = false;
		else {
			hasAvailableResources = true;
			notify();
		}
	}
	
	/***
	 * Called by the resource scheduler, in order to wait for resources to become 
	 * available
	 * @throws InterruptedException
	 */
	public synchronized void waitForResources() throws InterruptedException {
		wait();
	}
	
	/***
	 * Returns a list of immediate available resources.
	 * @return a list of immediate available resources
	 */
	public List<Resource> getAvailableResources() {
		List<Resource> availableResources = new ArrayList<Resource>();

		for (Resource resource : resourceList) {
			if (resource.isAvailable()) {
				availableResources.add(resource);
			}
		}

		return availableResources;
	}
	
	/***
	 * Returns the fastest available resource, by comparing the Resource.getProcessingSpeed()
	 * return value between available resources.
	 * @return The fastest available resource. Null if there is no available resource.
	 */
	public Resource getFastestAvailableResource() {
		List<Resource> availResList = getAvailableResources();
		Resource fastestRes = null;
		if (availResList.size() != 0) {
			fastestRes = availResList.get(0);
			for (Resource availRes: getAvailableResources()) {
				if (fastestRes.getProcessingSpeed() > availRes.getProcessingSpeed())
					fastestRes = availRes;
			}
		}
		
		return fastestRes;
	}
}
