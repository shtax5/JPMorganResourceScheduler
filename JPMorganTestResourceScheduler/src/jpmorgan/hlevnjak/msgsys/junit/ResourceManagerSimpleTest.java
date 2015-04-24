package jpmorgan.hlevnjak.msgsys.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.Resource;
import jpmorgan.hlevnjak.msgsys.ResourceManager;
import jpmorgan.hlevnjak.msgsys.impl.GatewaySimple;
import jpmorgan.hlevnjak.msgsys.impl.MessageImpl;
import jpmorgan.hlevnjak.msgsys.impl.ResourceManagerSimple;
import jpmorgan.hlevnjak.msgsys.impl.ResourceSimple;

import org.junit.BeforeClass;
import org.junit.Test;

public class ResourceManagerSimpleTest {

	private static ResourceManager resManager;
	private static List<Resource> resourceList;

	/**
	 * Holds the minimum and maximum processing times for each
	 * resource. For example, {100, 500} means that minimum processing time is
	 * 100ms, maximum processing time is 500ms. 
	 * FASTEST_RESOURCE_INDEX is the index of the fastest resource in this array.
	 */
	private static int[][] RESOURCE_PROCESSING_TIMES = { 
		{ 100, 500 },
		{ 500, 1000 }, 
	};

	private static String[] RESOURCE_NAME = { "1", "2", };

	private static int FASTEST_RESOURCE_INDEX = 0;

	private static int[] RESOURCE_PROCESSING_TIME_START_RESOURCE = { 100, 100 };

	/***
	 * Set up the tests by adding all the resources as defined in the array RESOURCE_PROCESSING_TIMES 
	 * to the resource manager.
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		resourceList = new ArrayList<Resource>();
		for (int i = 0; i < RESOURCE_PROCESSING_TIMES.length; i++) {
			Resource res = new ResourceSimple(RESOURCE_NAME[i],
					RESOURCE_PROCESSING_TIMES[i][0],
					RESOURCE_PROCESSING_TIMES[i][1]);
			resourceList.add(res);
		}
		resManager = new ResourceManagerSimple(resourceList);
	}
	
	/***
	 * Test the method getAvailableResources(). 
	 * The method should return the correct number of idle resources.
	 * @throws InterruptedException
	 */
	@Test
	public void testGetAvailableResources() throws InterruptedException {
		assertTrue(resManager.getAvailableResources().size() == RESOURCE_PROCESSING_TIMES.length);

		Iterator<Resource> it = resManager.getAvailableResources().iterator();

		int i = 0;
		if (it.hasNext()) {
			resManager.processMessage(it.next(), new MessageImpl(), new GatewaySimple());
			// One resource is running, number of available resources should be reduced by 1
			assertTrue(resManager.getAvailableResources().size() == RESOURCE_PROCESSING_TIMES.length - 1);
			// Wait for the resource to finish running
			Thread.sleep(RESOURCE_PROCESSING_TIMES[i][1]);
			// Resource finishes, number of available resources should get back to the previous value
			assertTrue(resManager.getAvailableResources().size() == RESOURCE_PROCESSING_TIMES.length);

			i++;
		}
	}

	/***
	 * Test the method getFastestResource().
	 * The method should return the resource with the lowest processing time. The index of the resource 
	 * should equal FASTEST_RESOURCE_INDEX, as specified by the test.
	 */
	@Test
	public void testGetFastestResource() {
		Resource fastestRes = resManager.getFastestAvailableResource();
		assertTrue(fastestRes.getName().equals(
				RESOURCE_NAME[FASTEST_RESOURCE_INDEX]));
	}

	/***
	 * Test the method startResource(Resource, Message, Gateway). 
	 * The method should successfuly start a resource to process a message, effectively calling the 
	 * message's complete() method, and set the hasAvailableResource attribute according to the 
	 * number of remaining resources.
	 * @throws InterruptedException
	 */
	@Test
	public void testStartResource() throws InterruptedException {
		resourceList = new ArrayList<Resource>();
		Resource res = new ResourceSimple("",
				RESOURCE_PROCESSING_TIME_START_RESOURCE[0],
				RESOURCE_PROCESSING_TIME_START_RESOURCE[1]);
		resourceList.add(res);

		resManager = new ResourceManagerSimple(resourceList);

		Message msg = new MessageImpl();

		resManager.processMessage(res, msg, new GatewaySimple());
		
		assertTrue(resManager.getAvailableResources().size() == 0);
		
		assertFalse(resManager.hasAvailableResources());
		assertFalse(res.isAvailable());
		
		Thread.sleep(RESOURCE_PROCESSING_TIME_START_RESOURCE[1]);
		assertTrue(resManager.hasAvailableResources());
		assertTrue(msg.isCompleted());
	}

	/***
	 * Test the method startResource(Message, Gateway).
	 * The method should choose the fastest resource available, then call 
	 * startResource(Resource, Message, Gateway).
	 * @throws InterruptedException
	 */
	@Test
	public void testStartFastestResource() throws InterruptedException {
		Resource fastestRes = resManager.getFastestAvailableResource();
		resManager.processMessage(new MessageImpl(), new GatewaySimple());
		assertTrue(resManager.getAvailableResources().size() == RESOURCE_PROCESSING_TIMES.length - 1);
		assertFalse(fastestRes.isAvailable());
	}
}
