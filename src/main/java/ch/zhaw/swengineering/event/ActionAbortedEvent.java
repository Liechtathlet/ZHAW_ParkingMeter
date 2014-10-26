package ch.zhaw.swengineering.event;

import java.util.EventObject;

/**
 * @author Daniel Brun
 * 
 *         Event which is trown if the action was aborted by the user.
 */
public class ActionAbortedEvent extends EventObject {

    /**
     * GUID.
     */
    private static final long serialVersionUID = -6815204578717099720L;

    /**
     * Creates a new instance of this class.
     * 
     * @param source
     *            the source.
     */
    public ActionAbortedEvent(final Object source) {
        super(source);
    }
}
