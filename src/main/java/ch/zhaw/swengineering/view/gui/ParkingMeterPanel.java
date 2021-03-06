/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import ch.zhaw.swengineering.view.gui.listeners.ActionAbortListener;
import ch.zhaw.swengineering.view.gui.listeners.NumberInputActionListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Roland Hofer
 * 
 *         Panel which displays a parking meter.
 */
public class ParkingMeterPanel extends JPanel implements
        DisplayTextAppenderInterface {

    /**
     * Generates Serial Version UID.
     */
    private static final long serialVersionUID = 1258929181774396319L;

    private static final Logger LOG = LogManager
            .getLogger(ParkingMeterPanel.class);

    /**
     * Static color-definitions
     */
    private static final Color BG = new Color(93, 92, 102);
    private static final Color BG_INNER = new Color(26, 51, 115);
    private static final Color BG_DISPLAY = new Color(162, 205, 90);
    private static final Color BG_CANCEL = new Color(204, 0, 0);
    private static final Color BG_OK = new Color(0, 153, 0);
    private static final Color BG_TICKETFIELD = new Color(92, 172, 238);

    private static final int INITAL_BUFFER_SIZE = 1;

    private double factor = 3.0;
    private int initialHeight;
    private int initialWidth;

    private JPanel parkingMeterPane;
    private JPanel feetPane;
    private JPanel parkingBorderPane1;
    private JPanel parkingMeterMMI;
    private JPanel buttonSlotPane;
    private JPanel ticketPane;
    private JPanel displayPane;
    private JPanel buttonPane;
    private JPanel ticketfieldPane;

    private JTextArea errorDisplay;
    private JTextArea infoDisplay;
    private JTextArea display;

    private List<JButton> numberBlockList;
    private NumberInputActionListener numberInputListener;

    private JButton buttonCancel = new JButton("C");
    private JButton buttonOk = new JButton("OK");

    private JTextArea ticketfield = new JTextArea();

    private CyclicBarrier barrier;

    private boolean actionExecuted;
    private boolean promptMode;
    private boolean blockNumberInput;
    private boolean cancelPressed;

    private String promptText;
    String lastErrorText;

    int currentInfoBufferElements;
    int infoMsgBufferSize;

    private ActionAbortListener actionAbortListener;

    /**
     * Creates a new parking meter panel.
     * 
     * @param anActionAbortListener
     *            The listener to execute when the cancel button was hit.
     */
    public ParkingMeterPanel(ActionAbortListener anActionAbortListener) {

        this.actionAbortListener = anActionAbortListener;

        // Init lock
        barrier = new CyclicBarrier(2);

        // Init helper objects
        actionExecuted = false;
        promptMode = false;
        blockNumberInput = false;
        cancelPressed = false;

        infoMsgBufferSize = INITAL_BUFFER_SIZE;
        currentInfoBufferElements = 0;
        initialHeight = 300;
        initialWidth = 120;

        setPreferredSize(new Dimension((int) (initialWidth * factor),
                (int) (initialHeight * factor)));

        numberBlockList = new ArrayList<>();

        numberInputListener = new NumberInputActionListener(this);

        parkingMeterPane = new JPanel();
        parkingBorderPane1 = new JPanel();
        parkingMeterMMI = new JPanel();
        feetPane = new JPanel();
        buttonSlotPane = new JPanel();
        ticketPane = new JPanel();
        displayPane = new JPanel();
        buttonPane = new JPanel();
        ticketfieldPane = new JPanel();

        BorderLayout borderLayoutParkingMeterPane = new BorderLayout();
        FlowLayout flowLayoutParkingBorderPane1 = new FlowLayout();
        FlowLayout flowLayoutParkingMeterMMI = new FlowLayout(
                FlowLayout.CENTER, 10, 10);
        FlowLayout flowLayoutParkingButtonSlotPane = new FlowLayout(
                FlowLayout.CENTER, 10, 20);
        BorderLayout borderLayoutParkingTicketPane = new BorderLayout();
        FlowLayout flowLayoutParkingDisplayPane = new FlowLayout(
                FlowLayout.CENTER, 5, 3);
        FlowLayout flowLayoutParkingTicketfieldPane = new FlowLayout(
                FlowLayout.CENTER, 5, 10);

        parkingMeterPane.setLayout(borderLayoutParkingMeterPane);
        parkingBorderPane1.setLayout(flowLayoutParkingBorderPane1);
        parkingMeterMMI.setLayout(flowLayoutParkingMeterMMI);
        buttonSlotPane.setLayout(flowLayoutParkingButtonSlotPane);
        ticketPane.setLayout(borderLayoutParkingTicketPane);
        displayPane.setLayout(flowLayoutParkingDisplayPane);
        buttonPane.setLayout(new GridLayout(4, 1, 5, 5));
        ticketfieldPane.setLayout(flowLayoutParkingTicketfieldPane);

        parkingMeterPane.setPreferredSize(new Dimension(
                (int) (initialWidth * factor), (int) (initialHeight * factor)));
        parkingMeterMMI.setPreferredSize(new Dimension(
                (int) ((initialWidth - 5) * factor),
                (int) ((initialHeight - 150) * factor)));
        feetPane.setPreferredSize(new Dimension((int) (initialWidth * factor),
                (int) ((initialHeight - 180) * factor)));
        buttonSlotPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 5) * factor),
                (int) ((initialHeight - 240) * factor)));
        ticketPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 5) * factor),
                (int) ((initialHeight - 250) * factor)));
        displayPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 20) * factor),
                (int) ((initialHeight - 250) * factor)));
        buttonPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 50) * factor),
                (int) ((initialHeight - 250) * factor)));
        ticketfieldPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 5) * factor),
                (int) ((initialHeight - 250) * factor)));

        this.setBackground(BG);
        parkingMeterPane.setBackground(BG_INNER);
        parkingMeterMMI.setBackground(BG_INNER);
        feetPane.setBackground(BG);
        buttonSlotPane.setBackground(BG_INNER);
        ticketPane.setBackground(BG_INNER);
        displayPane.setBackground(BG_DISPLAY);
        buttonPane.setBackground(BG_INNER);
        ticketfieldPane.setBackground(BG_INNER);

        this.add(parkingMeterPane, BorderLayout.CENTER);
        parkingMeterPane.add(feetPane, BorderLayout.SOUTH);
        parkingMeterPane.add(parkingMeterMMI, BorderLayout.CENTER);
        parkingMeterMMI.add(displayPane);
        parkingMeterMMI.add(buttonSlotPane);
        parkingMeterMMI.add(ticketPane);

        buttonSlotPane.add(buttonPane);

        ticketPane.add(ticketfieldPane, BorderLayout.WEST);

        display = new JTextArea();
        Dimension dimensionDisplay = new Dimension(new Dimension(
                (int) ((initialWidth - 20) * factor),
                (int) ((initialHeight - 250 - 42) * factor)));
        display.setPreferredSize(dimensionDisplay);
        display.setEditable(false);
        display.setWrapStyleWord(true);
        display.setLineWrap(true);
        display.setBackground(BG_DISPLAY);

        displayPane.add(display);

        infoDisplay = new JTextArea();
        Dimension dimensionInfoDisplay = new Dimension(new Dimension(
                (int) ((initialWidth - 20) * factor), (int) (25 * factor)));
        infoDisplay.setPreferredSize(dimensionInfoDisplay);
        infoDisplay.setEditable(false);
        infoDisplay.setWrapStyleWord(true);
        infoDisplay.setLineWrap(true);
        infoDisplay.setBackground(BG_DISPLAY);

        displayPane.add(infoDisplay);

        errorDisplay = new JTextArea();
        Dimension dimensionErrorDisplay = new Dimension(new Dimension(
                (int) ((initialWidth - 20) * factor), (int) (15 * factor)));
        errorDisplay.setPreferredSize(dimensionErrorDisplay);
        errorDisplay.setEditable(false);
        errorDisplay.setWrapStyleWord(true);
        errorDisplay.setLineWrap(true);
        errorDisplay.setBackground(BG_DISPLAY);

        displayPane.add(errorDisplay);

        // Create number buttons from 1 to 9
        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(i + "");
            numberButton.addActionListener(numberInputListener);
            numberBlockList.add(numberButton);

            buttonPane.add(numberButton);
        }

        // Crate zero button
        JButton numberButtonZero = new JButton("0");
        numberButtonZero.addActionListener(numberInputListener);
        numberBlockList.add(numberButtonZero);

        buttonCancel.setBackground(BG_CANCEL);
        buttonPane.add(buttonCancel);
        buttonPane.add(numberButtonZero);
        buttonOk.setBackground(BG_OK);
        buttonPane.add(buttonOk);

        ticketfield.setSize(10, 10);
        ticketfield.setColumns(7);
        ticketfield.setRows(4);
        ticketfield.setEditable(false);
        ticketfield.setBackground(BG_TICKETFIELD);
        ticketfieldPane.add(ticketfield);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                resetPromptDisplay();
                resetErrorDisplay();
                resetInfoDisplay();
                
                numberInputListener.reset();
                actionAbortListener.calledAbort();

                cancelPressed = true;
                actionExecuted = true;
                try {
                    barrier.await();
                } catch (Exception e) {
                    LOG.warn("Barrier failed...", e);
                }
            }
        });
        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae1) {
                actionExecuted = true;
                try {
                    barrier.await();
                } catch (Exception e) {
                    LOG.warn("Barrier failed...", e);
                }
                resetErrorDisplay();
                resetInfoDisplay();
            }
        });
    }

    /**
     * Interrupts the current waiting.
     */
    public void interruptWait() {
        actionExecuted = true;
        try {
            barrier.await();
        } catch (Exception e) {
            LOG.warn("Barrier failed...", e);
        }
    }

    /**
     * Waits for the ok button to be pressed.
     */
    public final boolean waitForOK() {
        boolean resultOK = true;
        // Set lock.
        try {
            do {
                barrier.await();
            } while (!actionExecuted);
            if (cancelPressed) {

                cancelPressed = false;
                resultOK = false;
            }
        } catch (InterruptedException e) {
            // Nothing to do here...didn't have monitor.
        } catch (BrokenBarrierException e) {
            LOG.warn("Barrier broken...", e);
        } finally {
            // Unset lock.
            actionExecuted = false;
            promptMode = false;
        }

        return resultOK;
    }

    /**
     * Reads an integer from the input.
     * 
     * @return the integer which was read.
     */
    public final Integer readInteger() {
        waitForOK();

        BigInteger returnInt = numberInputListener.getIntegerInput();

        if (returnInt != null) {
            return returnInt.intValue();
        } else {
            return null;
        }
    }

    @Override
    public void appendTextToPromptDisplay(String aText) {

        if (promptMode) {
            display.setText(promptText + aText);
        }
    }

    /**
     * Prints a text to the display.
     * 
     * @param aMessage
     *            the message.
     */
    public final void printPrompt(final String aMessage) {
        promptText = aMessage;
        promptMode = true;
        numberInputListener.reset();

        display.setText(promptText);

        if (lastErrorText != null
                && lastErrorText.equals(errorDisplay.getText())) {
            resetErrorDisplay();
        } else if (errorDisplay.getText().trim().length() > 0) {
            lastErrorText = errorDisplay.getText();
        }
    }

    /**
     * Prints an error message to the display.
     * 
     * @param aMessage
     *            the message to print.
     */
    public final void printError(final String aMessage) {
        errorDisplay.setText(aMessage);
    }

    /**
     * Prints an info message to the display.
     * 
     * @param aMessage
     *            the message to print.
     */
    public final void printInfo(final String aMessage) {
        if (currentInfoBufferElements >= infoMsgBufferSize) {
            resetInfoDisplay();
        }

        currentInfoBufferElements++;
        infoDisplay.append(aMessage);
        infoDisplay.append("\n");
    }

    /**
     * Resets the error display.
     */
    void resetErrorDisplay() {
        errorDisplay.setText("");
        lastErrorText = null;
    }

    /**
     * Resets the info display.
     */
    private void resetInfoDisplay() {
        infoDisplay.setText("");
        currentInfoBufferElements = 0;
        infoMsgBufferSize = INITAL_BUFFER_SIZE;
    }

    /**
     * Resets the prompt display.
     */
    public void resetPromptDisplay() {
        display.setText("");
    }

    /**
     * Sets the boolean that indicates, if the number block should be blocked or
     * not.
     * 
     * @param isBlocked
     *            True if the number block should be blocked.
     */
    public void setNumberBlockBlocked(final boolean isBlocked) {
        blockNumberInput = isBlocked;
        numberInputListener.setSuspendEvent(blockNumberInput);
    }

    /**
     * Increases the info buffer size temporarily.
     * 
     * @param aCount
     *            the count.
     */
    public void increaseInfoBufferSizeTemporarily(int aCount) {
        infoMsgBufferSize = aCount;
    }
}
