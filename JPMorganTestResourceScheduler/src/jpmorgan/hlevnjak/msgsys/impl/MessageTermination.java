package jpmorgan.hlevnjak.msgsys.impl;

import jpmorgan.hlevnjak.msgsys.Message;

/***
 * This class represents a termination message. This type of message is not supposed to be sent 
 * to the gateway, hence its "completed" status is always false.
 * @author mhlevnjak
 *
 */
public class MessageTermination extends Message {
	public static final String TERMINATION_MESSAGE = "TERMINATE";
	
	public MessageTermination(String group) {
		setGroup(group);
	}
		
	@Override
	public boolean isTermination() {
		return true;
	}

	/**
	 * Always return TERMINATION_MESSAGE for a termination message
	 */
	@Override
	public String getData() {
		return TERMINATION_MESSAGE;
	}

	@Override
	public String toString() {
		return TERMINATION_MESSAGE + " " + getGroup();
	}

	/**
	 * Do nothing. A termination message is not supposed to be sent to the gateway
	 */
	@Override
	public void completed() {}

	@Override
	public boolean isCompleted() {
		return false;
	}

}
