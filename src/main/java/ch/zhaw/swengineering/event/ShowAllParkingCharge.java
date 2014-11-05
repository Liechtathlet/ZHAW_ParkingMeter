package ch.zhaw.swengineering.event;

import java.util.EventObject;

/**
 * @author Roland Hofer
 * 
 *         Event which is thrown if the user will show all ParkingMeter Charge.
 */
public class ShowAllParkingCharge extends EventObject {

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
	public ShowAllParkingCharge(final Object source) {
		super(source);
	}
}
