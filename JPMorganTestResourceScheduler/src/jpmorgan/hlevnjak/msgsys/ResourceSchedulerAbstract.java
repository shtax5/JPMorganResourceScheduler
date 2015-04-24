package jpmorgan.hlevnjak.msgsys;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jpmorgan.hlevnjak.msgsys.exceptions.TerminatedGroupException;

/***
 * ResourceSchedulerAbstract contains the implementation of most necessary functionalities for 
 * the resource scheduler to operate. It needs a message prioritising algorithm in order to be 
 * fully functional.
 * @author mhlevnjak
 *
 */
public abstract class ResourceSchedulerAbstract extends ResourceScheduler {
	private ResourceManager resManager;
	private Queue<Message> messages;
	private Thread schedulerThread;
	private Gateway gateway;

	private List<String> cancelledGroup;
	private List<String> terminatedGroup;

	public ResourceSchedulerAbstract(ResourceManager resManager, Gateway gateway) {
		this.resManager = resManager;
		this.gateway = gateway;
		messages = new ConcurrentLinkedQueue<Message>();
		cancelledGroup = new ArrayList<String>();
		terminatedGroup = new ArrayList<String>();
	}

	@Override
	public void start() {
		if (schedulerThread == null)
			schedulerThread = new Thread(new SchedulerThread(resManager,
					messages));
		if (!schedulerThread.isAlive())
			schedulerThread.start();
	}
	
	/***
	 * Deals with both adding the message to the message queue 
	 * and termination messages
	 * @throws TerminatedGroupException 
	 */
	@Override
	public synchronized void addMessage(Message msg) throws TerminatedGroupException {
		synchronized (messages) {
			if (terminatedGroup.contains(msg.getGroup())) {
				// Throw a TerminatedGroupException
				throw new TerminatedGroupException();
			} else if (msg.isTermination()) 
				terminatedGroup.add(msg.getGroup());
			else 
				messages.add(msg);
		}
		notify();
	}

	/***
	 * This method should be called by another thread, in order for the thread to wait until messages 
	 * arrive in the message queue of the resource scheduler.
	 * @throws InterruptedException
	 */
	public synchronized void waitForMessages() throws InterruptedException {
		wait();
	}

	@Override
	public synchronized void cancelGroup(String group) {
		cancelledGroup.add(group);
	}

	/***
	 * This class is the main thread of the scheduler. Its main loop continuously waits for messages to 
	 * arrive, calls the prioritising algorithm, and deals with sending messages using the provided 
	 * resource manager
	 * @author buivuhoang
	 *
	 */
	private class SchedulerThread implements Runnable {
		private ResourceManager resManager;
		private Queue<Message> messages;

		public SchedulerThread(ResourceManager resManager,
				Queue<Message> messages) {
			this.resManager = resManager;
			this.messages = messages;
		}

		public void run() {
			while (true) {
				try {
					// Wait for messages to arrive
					System.out.println();
					System.out.println("Waiting for messages...");
					waitForMessages();
					System.out.println(messages);
					synchronized (messages) {
						// Messages arrived. Begin processing
						while (!messages.isEmpty()) {
							// Wait for resource to become available
							while (!resManager.hasAvailableResources()) {
								resManager.waitForResources();
								if (resManager.hasAvailableResources()) {
									System.out.println();
									break;
								}
							}

							// The prioritising algorithm takes place in this
							// method call
							Message msgToBeSent = messagePrioritisingAlgorithm(messages);

							// The actually sending is done here. If the message
							// belongs to a cancelled group, then it will not be
							// sent
							if (msgToBeSent != null) {
								if (!cancelledGroup.contains(msgToBeSent
										.getGroup())) {
									resManager.processMessage(msgToBeSent,
											gateway);
								}
							}

						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/***
	 * Rrepresents the algorithm of message prioritising. A concrete
	 * implementation of ResourceShedulerAbstract should implement this method,
	 * to provide more sophisticated prioritising algorithms, other than the
	 * default one. The default algorithm only returns the message on the top of
	 * the queue.
	 * 
	 * @param messages
	 *            - The message queue
	 * @return the message which should be sent next, according to the
	 *         prioritising algorithm
	 */
	public Message messagePrioritisingAlgorithm(Queue<Message> messages) {
		return messages.poll();
	}

}
