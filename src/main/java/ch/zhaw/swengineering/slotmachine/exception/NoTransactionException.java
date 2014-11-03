package ch.zhaw.swengineering.slotmachine.exception;

/**
 * @author Daniel Brun
 * 
 *         Exception which is thrown, when a transaction is needed and none was
 *         started.
 */
public class NoTransactionException extends Exception {

    /**
     * GUID.
     */
    private static final long serialVersionUID = -3066270149812161753L;

    /**
     * Creates a new instance of this class.
     * 
     * @param aMessage the message.
     */
    public NoTransactionException(String aMessage) {
        super(aMessage);
    }
}
