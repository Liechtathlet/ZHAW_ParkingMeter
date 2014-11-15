/**
 * 
 */
package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.view.gui.listeners.CoinInputActionListener;
import ch.zhaw.swengineering.view.gui.listeners.NumberInputActionListener;

/**
 * @author Roland Hofer
 * 
 *         Panel which displays a parking meter.
 */
public class ParkingMeterPanel extends JPanel implements ActionListener,
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
    private static final Color BG_SLUT = new Color(119, 136, 153);
    private static final Color BG_TICKETFIELD = new Color(92, 172, 238);

    private double factor = 3.0;
    private int initialHeight;
    private int initialWidth;

    private JPanel parkingMeterPane;
    private JPanel feetPane;
    private JPanel parkingBorderPane1;
    private JPanel parkingMeterMMI;
    private JPanel displayCoinsPane;
    private JPanel buttonSlotPane;
    private JPanel ticketPane;
    private JPanel displayPane;
    private JPanel coinsButtonPane;
    private JPanel buttonPane;
    private JPanel slotPane;
    private JPanel ticketfieldPane;

    private JTextArea display;

    private List<JButton> coinButtonList;
    private CoinInputActionListener coinInputListener;
    
    private List<JButton> numberBlockList;
    private NumberInputActionListener numberInputListener;

    private JButton buttonCancel = new JButton("C");
    private JButton buttonOk = new JButton("OK");

    private JTextArea coinSlut = new JTextArea("");
    private JTextArea ticketfield = new JTextArea();

    private CyclicBarrier barrier;
    
    private BigInteger integerInput;

    private boolean okButtonPressed;
    private boolean promptMode;
    private String promptText;

    /**
     * Creates a new parking meter panel.
     * 
     * @param someAvailableCoins
     *            The coins which should be available in the parking meter.
     */
    public ParkingMeterPanel(List<BigDecimal> someAvailableCoins) {

        // Init lock
        barrier = new CyclicBarrier(2);
        
        // Init helper objects
        integerInput = BigInteger.ZERO;
        okButtonPressed = false;
        promptMode = false;

        initialHeight = 300;
        initialWidth = 100;
        setPreferredSize(new Dimension((int) (initialWidth * factor),
                (int) (initialHeight * factor)));

        coinButtonList = new ArrayList<JButton>();
        numberBlockList = new ArrayList<JButton>();

        numberInputListener = new NumberInputActionListener(this);
        coinInputListener = new CoinInputActionListener(this);
        
        parkingMeterPane = new JPanel();
        parkingBorderPane1 = new JPanel();
        parkingMeterMMI = new JPanel();
        feetPane = new JPanel();
        displayCoinsPane = new JPanel();
        buttonSlotPane = new JPanel();
        ticketPane = new JPanel();
        displayPane = new JPanel();
        coinsButtonPane = new JPanel();
        buttonPane = new JPanel();
        slotPane = new JPanel();
        ticketfieldPane = new JPanel();

        BorderLayout borderLayoutParkingMeterPane = new BorderLayout();
        FlowLayout flowLayoutParkingBorderPane1 = new FlowLayout();
        FlowLayout flowLayoutParkingMeterMMI = new FlowLayout(
                FlowLayout.CENTER, 10, 10);
        FlowLayout flowLayoutParkingDisplayCoinsPane = new FlowLayout(
                FlowLayout.CENTER, 10, 10);
        FlowLayout flowLayoutParkingButtonSlotPane = new FlowLayout(
                FlowLayout.CENTER, 10, 20);
        BorderLayout borderLayoutParkingTicketPane = new BorderLayout();
        FlowLayout flowLayoutParkingDisplayPane = new FlowLayout(
                FlowLayout.CENTER, 5, 10);
        FlowLayout flowLayoutParkingCoinsButtonPane = new FlowLayout(
                FlowLayout.CENTER, 5, 10);
        FlowLayout flowLayoutParkingSlotPane = new FlowLayout();
        FlowLayout flowLayoutParkingTicketfieldPane = new FlowLayout(
                FlowLayout.CENTER, 5, 10);

        parkingMeterPane.setLayout(borderLayoutParkingMeterPane);
        parkingBorderPane1.setLayout(flowLayoutParkingBorderPane1);
        parkingMeterMMI.setLayout(flowLayoutParkingMeterMMI);
        displayCoinsPane.setLayout(flowLayoutParkingDisplayCoinsPane);
        buttonSlotPane.setLayout(flowLayoutParkingButtonSlotPane);
        ticketPane.setLayout(borderLayoutParkingTicketPane);
        displayPane.setLayout(flowLayoutParkingDisplayPane);
        coinsButtonPane.setLayout(flowLayoutParkingCoinsButtonPane);
        buttonPane.setLayout(new GridLayout(4, 1, 5, 5));
        slotPane.setLayout(flowLayoutParkingSlotPane);
        ticketfieldPane.setLayout(flowLayoutParkingTicketfieldPane);

        parkingMeterPane.setPreferredSize(new Dimension(
                (int) (initialWidth * factor), (int) (initialHeight * factor)));
        parkingMeterMMI.setPreferredSize(new Dimension(
                (int) ((initialWidth - 10) * factor),
                (int) ((initialHeight - 150) * factor)));
        feetPane.setPreferredSize(new Dimension((int) (initialWidth * factor),
                (int) ((initialHeight - 180) * factor)));
        displayCoinsPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 10) * factor),
                (int) ((initialHeight - 245) * factor)));
        buttonSlotPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 10) * factor),
                (int) ((initialHeight - 240) * factor)));
        ticketPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 10) * factor),
                (int) ((initialHeight - 250) * factor)));
        displayPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 50) * factor),
                (int) ((initialHeight - 250) * factor)));
        coinsButtonPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 70) * factor),
                (int) ((initialHeight - 250) * factor)));
        buttonPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 45) * factor),
                (int) ((initialHeight - 250) * factor)));
        slotPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 75) * factor),
                (int) ((initialHeight - 250) * factor)));
        ticketfieldPane.setPreferredSize(new Dimension(
                (int) ((initialWidth - 10) * factor),
                (int) ((initialHeight - 250) * factor)));

        this.setBackground(BG);
        parkingMeterPane.setBackground(BG_INNER);
        parkingMeterMMI.setBackground(BG_INNER);
        feetPane.setBackground(BG);
        displayCoinsPane.setBackground(BG_INNER);
        buttonSlotPane.setBackground(BG_INNER);
        ticketPane.setBackground(BG_INNER);
        displayPane.setBackground(BG_DISPLAY);
        coinsButtonPane.setBackground(BG_INNER);
        buttonPane.setBackground(BG_INNER);
        slotPane.setBackground(BG_INNER);
        ticketfieldPane.setBackground(BG_INNER);

        this.add(parkingMeterPane, BorderLayout.CENTER);
        parkingMeterPane.add(feetPane, BorderLayout.SOUTH);
        parkingMeterPane.add(parkingMeterMMI, BorderLayout.CENTER);
        parkingMeterMMI.add(displayCoinsPane);
        parkingMeterMMI.add(buttonSlotPane);
        parkingMeterMMI.add(ticketPane);

        displayCoinsPane.add(displayPane);
        displayCoinsPane.add(coinsButtonPane);

        buttonSlotPane.add(buttonPane);
        buttonSlotPane.add(slotPane);

        ticketPane.add(ticketfieldPane, BorderLayout.WEST);

        display = new JTextArea();
        Dimension dimensionDisplay = new Dimension(new Dimension(
                (int) ((initialWidth - 50) * factor),
                (int) ((initialHeight - 250) * factor)));
        display.setPreferredSize(dimensionDisplay);
        display.setEditable(false);
        display.setWrapStyleWord(true);
        display.setLineWrap(true);
        display.setBackground(BG_DISPLAY);

        displayPane.add(display);

        // Create coin buttons
        for (BigDecimal coinValue : someAvailableCoins) {
            JButton coinBtn = new JButton(coinValue.toString());
            // TODO: evtl. hash map with mapping.
            coinBtn.addActionListener(coinInputListener);
            coinButtonList.add(coinBtn);
            coinsButtonPane.add(coinBtn);
        }

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

        coinSlut.setSize(10, 40);
        coinSlut.setColumns(1);
        coinSlut.setRows(4);
        coinSlut.setEditable(false);
        coinSlut.setBackground(BG_SLUT);
        slotPane.add(coinSlut);

        ticketfield.setSize(10, 10);
        ticketfield.setColumns(7);
        ticketfield.setRows(4);
        ticketfield.setEditable(false);
        ticketfield.setBackground(BG_TICKETFIELD);
        ticketfieldPane.add(ticketfield);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                numberInputListener.reset();
                //TODO: abort all event
            }
        });
        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae1) {
                okButtonPressed = true;
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent anEvent) {

    }

    /**
     * Prints a text to the display.
     * 
     * @param aMessage
     *            the message.
     * @param prompt
     *            True if the printed text is an input prompt
     */
    public final void print(final String aMessage, final boolean prompt) {
        if (prompt) {
            promptText = aMessage;
            promptMode = true;
            numberInputListener.reset();
        }

        display.setText(aMessage);
    }

    /**
     * Waits for the ok button to be pressed.
     */
    private final void waitForRead() {
        // Set lock.
        try {
            do {
                barrier.await();
            } while (!okButtonPressed);
        } catch (InterruptedException e) {
            // Nothing to do here...didn't have monitor.
        } catch (BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // Unset lock.
            okButtonPressed = false;
            promptMode = false;
        }
    }

    /**
     * Reads an integer from the input.
     * 
     * @return the integer which was read.
     */
    public final Integer readInteger() {
        waitForRead();

        BigInteger returnInt = numberInputListener.getIntegerInput();
        return Integer.valueOf(returnInt.intValue());
    }

    /**
     * Reads the current coin value from the input.
     * 
     * @return the coin value.
     */
    public final BigDecimal readCoinValue() {
        waitForRead();

        // TODO: implement

        return null;
    }

    @Override
    public void appendTextToDisplay(String aText) {
        
        //TODO: prompt mode unterscheiden -> coin insert / number insert...
        if (promptMode) {
            display.setText(promptText + aText);
        }
    }
}
