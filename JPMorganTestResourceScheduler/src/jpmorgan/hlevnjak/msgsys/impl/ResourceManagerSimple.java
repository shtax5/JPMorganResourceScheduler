package jpmorgan.hlevnjak.msgsys.impl;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import jpmorgan.hlevnjak.msgsys.Gateway;
import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.Resource;
import jpmorgan.hlevnjak.msgsys.ResourceManager;

/***
 * A simple implementation of ResourceManager. This class manages resources by creating a 
 * thread for each RUNNING resource that watches for the progress of that resource.
 * @author mhlevnjak
 *
 */
public class ResourceManagerSimple extends ResourceManager {

	public ResourceManagerSimple() {
		super();
	}

	public ResourceManagerSimple(List<Resource> resourceList) {
		super(resourceList);
	}

	@Override
	public void addResource(String name) {
		Resource res = new ResourceSimple(name);
		this.resourceList.add(res);
		hasAvailableResources = true;
	}

	/***
	 * This method is called by the user to provide resources to the resource manager. 
	 * The user can specify the lower and upper limit of processing time that the resource has. 
	 * For this simulation, the actual processing time is selected randomly between the two values.
	 * @param name - The name of the resource
	 * @param minProcessingTime - The minimum processing time needed for the resource
	 * @param maxProcessingTime - The maximum processing time needed for the resource
	 */
	public void addResource(String name, int minProcessingTime,
			int maxProcessingTime) {
		Resource res = new ResourceSimple(name, minProcessingTime,
				maxProcessingTime);
		this.resourceList.add(res);
		hasAvailableResources = true;
	}
	
	/***
	 * This implementation tries to process given message with the fastest available resource at the time.
	 */
	@Override
	public void processMessage(Message msg, Gateway gateway) throws InterruptedException {
		Resource fastestRes = this.getFastestAvailableResource();
		if (fastestRes != null) {
			processMessage(fastestRes, msg, gateway);
		}
	}
	
	@Override
	public synchronized void processMessage(Resource res, Message msg, Gateway gateway) throws InterruptedException {
		/**
		 * Assign a CountDownLatch to both the ResourceCheckThread and the resource thread
		 * to update the status of hasAvailableResource accordingly.
		 */
		CountDownLatch resourceEnds = new CountDownLatch(1);
		Thread resManagerThread = new Thread(new ResourceCheckThread(resourceEnds));
		resManagerThread.start();
		res.start(msg, gateway, resourceEnds);
		updateAvailableResource();
		
		System.out.print("Sent: " + msg + ", ");
	}

	/***
	 * This thread is instantiated and started every time a resource is started, to keep track of when the resource 
	 * finishes and how long the process takes.
	 * @author buivuhoang
	 *
	 */
	private class ResourceCheckThread implements Runnable {
		private final CountDownLatch resourceEnds;

		public ResourceCheckThread(CountDownLatch resourceEnds) {
			this.resourceEnds = resourceEnds;
		}

		@Override
		public void run() {
			try {
				resourceEnds.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				updateAvailableResource();
			}
		}
	}
}
