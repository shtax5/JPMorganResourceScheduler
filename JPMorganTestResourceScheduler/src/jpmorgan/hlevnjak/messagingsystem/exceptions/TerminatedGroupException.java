package jpmorgan.hlevnjak.messagingsystem.exceptions;

/***
 * Raised when the resource scheduler receives a message that belongs 
 * to a terminated group.
 * @author mhlevnjak
 *
 */
public class TerminatedGroupException extends Exception {

	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long serialVersionUID = -6152901025706255802L;
	private static final String ERROR_MESSAGE = "Received a message from a terminated group.";
	
	public TerminatedGroupException(String message) {
		super(ERROR_MESSAGE + " " + message);
	}
	
	public TerminatedGroupException() {
		super(ERROR_MESSAGE);
	}
}

