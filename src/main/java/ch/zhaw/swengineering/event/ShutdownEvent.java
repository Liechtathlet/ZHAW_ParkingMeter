package ch.zhaw.swengineering.event;

import java.util.EventObject;

/**
 * @author Daniel Brun
 * 
 *         Event which is thrown if the user shuts down the ParkingMeter.
 */
public class ShutdownEvent extends EventObject {

    /**
     * GUID.
     */
    private static final long serialVersionUID = 7943236822168309195L;

    /**
     * Creates a new instance of this class.
     * 
     * @param source
     *            the source.
     */
    public ShutdownEvent(final Object source) {
        super(source);
    }
}
