package ch.zhaw.swengineering.event;

import java.util.EventObject;

/**
 * @author Daniel Brun
 * 
 *         Event which is thrown if the action was aborted by the user.
 *         TODO: Evtl. ReName Event
 */
public class MoneyInsertedEvent extends EventObject {

	/**
     * GUID.
     */
    private static final long serialVersionUID = 7257553615509542001L;
    private final float droppingInMoney;
    /**
	 * Creates a new instance of this class.
	 * @param source the source object.
	 */
	public MoneyInsertedEvent(final Object source, float droppingInMoney) {

        super(source);
        this.droppingInMoney = droppingInMoney;
	}


}
