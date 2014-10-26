package ch.zhaw.swengineering.helper;

/**
 * @author Daniel Brun Helper Class for Parameter validation.
 */
public final class AssertHelper {

    /**
     * Hide default constructor.
     */
    private AssertHelper() {
        // Noting to do here.
    }

    /**
     * Checks if the given object is null.
     * If the object is null, an IllegalArgumentException is thrown.
     * 
     * @param anObject
     *            The object to validate.
     * @param aName
     *            The name of the parameter.
     */
    public static void isNotNull(final Object anObject, final String aName) {
        if (anObject == null) {
            throw new IllegalArgumentException("The argument '" + aName
                    + "' must not be null!");
        }
    }
}
