package jpmorgan.hlevnjak.msgsys.junit;

import static org.junit.Assert.assertTrue;
import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.MessageFactory;
import jpmorgan.hlevnjak.msgsys.impl.MessageFactoryImpl;
import jpmorgan.hlevnjak.msgsys.impl.MessageImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageFactoryImplTest {
	
	private static final int MESSAGE_INDEX = 2;
	private static final String GROUP_2 = "2";
	private static final String GROUP_1 = "1";
	private MessageFactory msgFactory;
	
	@Before
	public void setUp() throws Exception {
		msgFactory = new MessageFactoryImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	/***
	 * Test the method createMessage(). 
	 * The message's group and name attributes should be 
	 * correctly set by the message factory.
	 */
	@Test
	public void testCreateMessage() {
		Message msg1 = msgFactory.createMessage(GROUP_1);
		Message msg2 = new MessageImpl(MESSAGE_INDEX - 1, GROUP_1);
		for (int i = 0; i < MESSAGE_INDEX - 1; i++) {
			msg2 = msgFactory.createMessage(GROUP_2);
		}
		
		assertTrue(msg1.getGroup().equals(GROUP_1));
		assertTrue(msg2.getName().equals(MESSAGE_INDEX + ""));
	}

}
