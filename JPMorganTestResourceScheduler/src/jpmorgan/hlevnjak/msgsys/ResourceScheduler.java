package jpmorgan.hlevnjak.msgsys;

import jpmorgan.hlevnjak.msgsys.exceptions.TerminatedGroupException;

/***
 * ResourceScheduler has the responsibility to listen to incoming messages, and process them as resources 
 * become available. ResourceScheduler utilises ResourceManager, in order to invoke Resources to process 
 * Messages. ResourceScheduler can also cancel a group of Messages, or receive Termination nessages.
 * @author mhlevnjak
 *
 */
public abstract class ResourceScheduler {
	
	/***
	 * Called by the user, to add a new message to the queue. The new message can be a normal
	 * message or a termination message.
	 * @param msg
	 * @throws TerminatedGroupException 
	 */
	public abstract void addMessage(Message msg) throws TerminatedGroupException;
	
	/***
	 * Starts the main thread of ResourceScheduler. Once started, the scheduler will wait for 
	 * messages to arrive.
	 */
	public abstract void start();
	
	/***
	 * Cancels a group of message. Once cancelled, no more message of that group will be sent to the gateway.
	 * @param group
	 */
	public abstract void cancelGroup(String group);
	
	
}
