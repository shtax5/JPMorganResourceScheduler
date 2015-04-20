package jpmorgan.hlevnjak.messagingsystem.impl;

import java.util.Iterator;
import java.util.Queue;

import jpmorgan.hlevnjak.messagingsystem.Gateway;
import jpmorgan.hlevnjak.messagingsystem.Message;
import jpmorgan.hlevnjak.messagingsystem.ResourceManager;
import jpmorgan.hlevnjak.messagingsystem.ResourceSchedulerAbstract;

/***
 * An implementation of ResourceScheduler to include the message prioritising algorithm 
 * for Groups. The scheduler will try to match the messages within the same group together, 
 * starting from the first group that arrives in the queue.
 * @author mhlevnjak
 *
 */
public class ResourceSchedulerGroupPriority extends ResourceSchedulerAbstract {
	private String lastMsgGroup = "";

	public ResourceSchedulerGroupPriority(ResourceManager resManager,
			Gateway gateway) {
		super(resManager, gateway);
	}

	@Override
	public Message messagePrioritisingAlgorithm(Queue<Message> messages) {
		Message msgToBeSent = null;
		Message msg = messages.peek();
		boolean canSend = canSendTopMessageGroupPriority(msg, lastMsgGroup,
				groupExistsInQueue(lastMsgGroup, messages));

		if (canSend) {
			// If the message can be send, send it
			msgToBeSent = messages.poll();
			lastMsgGroup = msgToBeSent.getGroup();
		} else {
			// If the message can't be send, we pop out the next closest one
			// that belongs to lastMsgGroup
			Message nextSendableMsg = findNextSendableMessage(lastMsgGroup,
					messages);
			// Found the message, send it, and remove it from the queue
			if (messages.contains(nextSendableMsg)) {
				msgToBeSent = nextSendableMsg;
				messages.remove(nextSendableMsg);
			}
		}
		return msgToBeSent;
	}
	
	/***
	 * Decides if the input message (presumably on top of the queue) can be sent to the gateway. 
	 * The message can be sent: 
	 *  + if it belongs to the same group as the last sent message, or 
	 *  + if more than 1 resource is available, or 
	 *  + if it does not belong to the same group, but there is no more message of that group in the queue.
	 * @param msg - The input message
	 * @param lastMsgGroup - The group of the last sent message
	 * @param numOfAvailableResource - The number of available resources
	 * @return
	 */
	public boolean canSendTopMessageGroupPriority(Message msg, String lastMsgGroup, boolean lastGroupExistsInQueue) {
		if (msg.getGroup().equals(lastMsgGroup) || !lastGroupExistsInQueue) //numOfAvailableResource > 1 ||
			return true;
		return false;
	}
	
	/**
	 * Check if the message queue contains a certain message group.
	 * @param group - The group of message to check
	 * @param msgQueue - The message queue
	 * @return true if the group is found in the queue
	 */
	private boolean groupExistsInQueue(String group, Queue<Message> msgQueue) {
		for (Message msg : msgQueue) {
			if (msg.getGroup().equals(group))
				return true;
		}
		return false;
	}

	/**
	 * Looks for the next suitable message to be sent to the gateway. This 
	 * is an utility method as part of the group prioritising algorithm. A suitable message is the 
	 * first message found in the queue that belongs to the same group as the last sent message.
	 * @param lastMsgGroup - The group of the last sent message
	 * @param msgQueue - The message queue
	 * @return the suitable message to be sent. Null if not found any.
	 */
	private Message findNextSendableMessage(String lastMsgGroup,
			Queue<Message> msgQueue) {
		Iterator<Message> it = msgQueue.iterator();
		// Skip the first element, because it is the one which we know for sure
		// that cannot be sent
		if (it.hasNext())
			it.next();
		while (it.hasNext()) {
			Message msg = it.next();
			if (msg.getGroup().equals(lastMsgGroup))
				return msg;
		}
		return null;
	}
}
