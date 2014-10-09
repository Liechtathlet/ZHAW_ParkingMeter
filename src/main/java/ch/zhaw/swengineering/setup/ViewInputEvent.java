/**
 * 
 */
package ch.zhaw.swengineering.setup;

import java.util.EventObject;

/**
 * @author Daniel Brun
 *
 * Represents an event for an input which is made in a view implementation.
 */
public class ViewInputEvent extends EventObject {

	/**
	 * Generated Serial Version UID.
	 */
	private static final long serialVersionUID = 8399023607197384009L;
	
	private String input;
	
	/**
	 * Creates a new ViewInputEvent.
	 * 
	 * @param source The source of the event.
	 * @param input The content / input of the event.
	 */
	public ViewInputEvent(Object source,String input) {
		super(source);
		
		this.input = input;
	}

	public String getInput() {
		return input;
	}
}
