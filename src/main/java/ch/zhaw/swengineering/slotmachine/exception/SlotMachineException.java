package ch.zhaw.swengineering.slotmachine.exception;


/**
 * @author Daniel Brun
 * 
 *         Exception which is thrown when an unexpected error occurred in the
 *         slot machine.
 */
public class SlotMachineException extends RuntimeException {

    /**
     * Creates a new instance of this class.
     * 
     * @param aMessage
     *            the message.
     */
    public SlotMachineException(String aMessage) {
        super(aMessage);
    }
}
