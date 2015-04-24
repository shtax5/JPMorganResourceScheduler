package jpmorgan.hlevnjak.msgsys.junit;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.MessageFactory;
import jpmorgan.hlevnjak.msgsys.impl.MessageFactoryImpl;
import jpmorgan.hlevnjak.msgsys.impl.ResourceSchedulerGroupPriority;

import org.junit.Test;

public class ResourceSchedulerGroupPriorityTest {

	private MessageFactory msgFactory;
	
	/**
	 * Test cases for optimising queue.
	 * The array MESSAGE_QUEUE_TEST_CASES contains the Group data for the input list of messages
	 * The array RESULT_CANSEND_TEST_CASES contains the expected output for the canSendTopMessage method
	 */
	private static String[][] MESSAGE_QUEUE_TEST_CASES = { 
		{"4", "2", "1", "3", "2"}, 			// Case 1
		{"1", "3", "2", "1", "2"}, 			// Case 2
	};

	private static boolean[][] RESULT_CANSEND_TEST_CASES = {
		{true, false, true, true, true}, 	// Case 1
		{true, false, false, true, true},	// Case 2
	};
	
	/***
	 * Test the method canSendTopMessage(). 
	 * Given the inputs as specified by the test, the method should return the correct 
	 * boolean values. 
	 */
	@Test
	public void testCanSendTopMessage() {
		ResourceSchedulerGroupPriority rs = new ResourceSchedulerGroupPriority(null, null);
		msgFactory = new MessageFactoryImpl();
		for (int i = 0; i < MESSAGE_QUEUE_TEST_CASES.length; i++) {
			String lastGroup = "";
			
			// Add messages to a queue
			Queue<String> msgQueue = new ArrayBlockingQueue<String>(
					MESSAGE_QUEUE_TEST_CASES[i].length);
			for (int j = 0; j < MESSAGE_QUEUE_TEST_CASES[i].length; j++)
				msgQueue.add(MESSAGE_QUEUE_TEST_CASES[i][j]);

			boolean[] canSend = new boolean[msgQueue.size()];
			
			int j = 0;
			while (msgQueue.size() > 0) {
				Message msg = msgFactory.createMessage(msgQueue.poll());
				// Call the method to populate the boolean array
				canSend[j] = rs.canSendTopMessageGroupPriority(msg, lastGroup, msgQueue.contains(lastGroup));

				if (canSend[j])
					lastGroup = msg.getGroup();
				
				j++;
			}
			
			assertTrue(Arrays.equals(canSend, RESULT_CANSEND_TEST_CASES[i]));
		}
	}
}
