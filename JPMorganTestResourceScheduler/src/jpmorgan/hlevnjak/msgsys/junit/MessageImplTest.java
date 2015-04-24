package jpmorgan.hlevnjak.msgsys.junit;

import static org.junit.Assert.*;
import jpmorgan.hlevnjak.msgsys.Message;
import jpmorgan.hlevnjak.msgsys.impl.MessageImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageImplTest {
	
	private static final String TEST_NAME_GROUP = "TEST_NAME";
	private static final String MSG_GROUP = "1";
	private static final int MSG_INDEX = 1;
	private Message msg;
	
	@Before
	public void setUp() throws Exception {
		msg = new MessageImpl(MSG_INDEX, MSG_GROUP);
	}

	@After
	public void tearDown() throws Exception {
	}

	/***
	 * Test the method completed(). 
	 * The method should return false if completed() is not yet called, 
	 * and true if completed() is called.
	 */
	@Test
	public void testCompleted() {
		assertFalse(msg.isCompleted());
		msg.completed();
		assertTrue(msg.isCompleted());
	}
	
	/***
	 * Test the method setName(). 
	 * The name of the message should be correctly set.
	 */
	@Test
	public void testSetName() {
		msg.setName(TEST_NAME_GROUP);
		assertTrue(msg.getName().equals(TEST_NAME_GROUP));
	}

	/***
	 * Test the method setGroup(). 
	 * The group of the message should be correctly set.
	 */
	@Test
	public void testSetGroup() {
		msg.setGroup(TEST_NAME_GROUP);
		assertTrue(msg.getGroup().equals(TEST_NAME_GROUP));
	}

}
