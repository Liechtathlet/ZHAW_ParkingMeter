package ch.zhaw.swengineering.slotmachine.exception;

/**
 * @author Daniel Brun
 * 
 *         Exception which is thrown, when another transaction is already
 *         active.
 */
public class TransactionAlreadyStartedException extends RuntimeException {

    /**
     * GUID.
     */
    private static final long serialVersionUID = 5195177805612208469L;

    /**
     * Creates a new instance of this class.
     * 
     * @param message
     *            the message.
     */
    public TransactionAlreadyStartedException(String message) {
        super(message);
    }
}
