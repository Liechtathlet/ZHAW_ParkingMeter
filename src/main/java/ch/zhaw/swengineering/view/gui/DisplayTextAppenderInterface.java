package ch.zhaw.swengineering.view.gui;

/**
 * @author Daniel Brun
 * 
 *         Interface for display a text.
 */
public interface DisplayTextAppenderInterface {

    /**
     * Appends the given text to the display if the display is in prompt mode.
     * 
     * @param aText
     *            the text to append.
     */
    void appendTextToDisplay(String aText);
}
