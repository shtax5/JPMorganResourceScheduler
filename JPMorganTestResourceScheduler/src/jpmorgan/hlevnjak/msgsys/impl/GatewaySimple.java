package jpmorgan.hlevnjak.msgsys.impl;

import jpmorgan.hlevnjak.msgsys.Gateway;
import jpmorgan.hlevnjak.msgsys.Message;

/***
 * A simple implementation of the Gateway interface.
 * @author mhlevnjak
 *
 */
public class GatewaySimple implements Gateway {

	/***
	 * Very simple implementation of send(). Just call the method completed()
	 * of the Message object, without any further processing.
	 */
	@Override
	public void send(Message msg) {
		if (!msg.isCompleted())
			msg.completed();
	}

}
