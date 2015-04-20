package jpmorgan.hlevnjak.messagingsystem.impl;

import jpmorgan.hlevnjak.messagingsystem.Gateway;
import jpmorgan.hlevnjak.messagingsystem.Message;

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
