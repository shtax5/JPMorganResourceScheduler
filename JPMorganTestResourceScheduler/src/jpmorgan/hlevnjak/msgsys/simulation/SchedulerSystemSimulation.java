package jpmorgan.hlevnjak.msgsys.simulation;

import jpmorgan.hlevnjak.msgsys.Gateway;
import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.MessageFactory;
import jpmorgan.hlevnjak.msgsys.ResourceManager;
import jpmorgan.hlevnjak.msgsys.ResourceScheduler;
import jpmorgan.hlevnjak.msgsys.exceptions.TerminatedGroupException;
import jpmorgan.hlevnjak.msgsys.impl.GatewaySimple;
import jpmorgan.hlevnjak.msgsys.impl.MessageFactoryImpl;
import jpmorgan.hlevnjak.msgsys.impl.ResourceManagerSimple;
import jpmorgan.hlevnjak.msgsys.impl.ResourceSchedulerGroupPriority;

/***
 * This is the starting point of the program simulation. 
 * The whole scheduler system is initialised and executed. 
 * 
 * IMPORTANT: Note that the system may seem to run SLOWLY. However, that is due to 
 * the 1 - 2 seconds of sleeping time of Resource, as a way to simulate the delay 
 * caused by remote resources running in real-time. 
 * If this delay is removed, the system will perform tasks almost instantaneously. 
 * To remove the delay, modify the values of both minProcessingTime and maxProcessingTime 
 * in ResourceSimple to 0. 
 * 
 * To make the process more visible, several System.out.print commands have been put 
 * throughout the system. One can be found in 
 * ResourceManagerSimple.processMessage(Resource, Message, Gateway), the others are in 
 * ResourceSchedulerAbstract.SchedulerThread.run().
 * @author mhlevnjak
 *
 */
public class SchedulerSystemSimulation {
	public static void main(String[] args) throws InterruptedException, TerminatedGroupException {
		Gateway gateway = new GatewaySimple();
		ResourceManager resManager = new ResourceManagerSimple();
		ResourceScheduler rs = new ResourceSchedulerGroupPriority(resManager,
				gateway);
		
		// Start the scheduler
		rs.start();
		MessageFactory mf = new MessageFactoryImpl();
		
		// Can add resources before or after starting the scheduler
		resManager.addResource("1");

		String[][] MESSAGE_QUEUE_TEST_CASES = { 
				{ "2", "1", "2", "3" }, // Case 1
		};

		for (int i = 0; i < MESSAGE_QUEUE_TEST_CASES.length; i++) {
			for (int j = 0; j < MESSAGE_QUEUE_TEST_CASES[i].length; j++) {
				Message msg = mf.createMessage(MESSAGE_QUEUE_TEST_CASES[i][j]);

				rs.addMessage(msg);
			}
		}

	}
}
