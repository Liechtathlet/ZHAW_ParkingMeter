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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * @author Daniel Brun
 * 
 *         Panel which displays a parking meter.
 */
public class ParkingMeterPanel extends JPanel {

	/**
	 * Generates Serial Version UID.
	 */
	private static final long serialVersionUID = 1258929181774396319L;

	private static final Color BG = new Color(93, 92, 102);
	private static final Color BG_INNER = new Color(26, 51, 115);
	private static final Color BG_DISPLAY = new Color(162, 205, 90);
	private static final Color BG_CANCEL = new Color(204, 0, 0);
	private static final Color BG_OK = new Color(0, 153, 0);
	private static final Color BG_SLUT = new Color(119, 136, 153);
	private static final Color BG_TICKETFIELD = new Color(92, 172, 238);

	// private static final Color BG_MARKER = new Color(200, 139, 50);
	// private static final Color BG_NUMBER_FIELD = new Color(200, 207, 215);

	private double factor = 3.0;
	private int initialHeight;
	private int initialWidth;

	private static JPanel parkingMeterPane;
	private static JPanel parkingBorderPane1;
	private static JPanel feetPane;
	private static JPanel parkingMeterMMI;
	private static JPanel displayCoinsPane;
	private static JPanel buttonSlotPane;
	private static JPanel ticketPane;
	private static JPanel displayPane;
	private static JPanel coinsButtonPane;
	private static JPanel buttonPane;
	private static JPanel slotPane;
	private static JPanel ticketfieldPane;

	private static JTextArea display;

	private static JButton coin1 = new JButton("Wert1");
	private static JButton coin2 = new JButton("Wert2");
	private static JButton coin3 = new JButton("Wert3");
	private static JButton coin4 = new JButton("Wert4");

	private static JButton button1 = new JButton("1");
	private static JButton button2 = new JButton("2");
	private static JButton button3 = new JButton("3");
	private static JButton button4 = new JButton("4");
	private static JButton button5 = new JButton("5");
	private static JButton button6 = new JButton("6");
	private static JButton button7 = new JButton("7");
	private static JButton button8 = new JButton("8");
	private static JButton button9 = new JButton("9");
	private static JButton button0 = new JButton("0");
	private static JButton buttonCancel = new JButton("C");
	private static JButton buttonOk = new JButton("OK");

	private static JTextArea coinSlut = new JTextArea("");
	private static JTextArea ticketfield = new JTextArea();

	public ParkingMeterPanel() {

		initialHeight = 300;
		initialWidth = 100;
		setPreferredSize(new Dimension((int) (initialWidth * factor),
				(int) (initialHeight * factor)));

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
		// coinsButtonPane.setLayout(new GridLayout(3, 1, 5, 5));
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
		Dimension dimensionDisplay = new Dimension((int) (45 * factor),
				(int) (20 * factor));
		display.setPreferredSize(dimensionDisplay);
		display.setEditable(false);
		display.setBackground(BG_DISPLAY);

		displayPane.add(display);

		coinsButtonPane.add(coin1);
		coinsButtonPane.add(coin2);
		coinsButtonPane.add(coin3);
		coinsButtonPane.add(coin4);

		buttonPane.add(button1);
		buttonPane.add(button2);
		buttonPane.add(button3);
		buttonPane.add(button4);
		buttonPane.add(button5);
		buttonPane.add(button6);
		buttonPane.add(button7);
		buttonPane.add(button8);
		buttonPane.add(button9);
		buttonCancel.setBackground(BG_CANCEL);
		buttonPane.add(buttonCancel);
		buttonPane.add(button0);
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

		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "1");
			}
		});
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "2");
			}
		});
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "3");
			}
		});
		button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "4");
			}
		});
		button5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "5");
			}
		});
		button6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "6");
			}
		});
		button7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "7");
			}
		});
		button8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "8");
			}
		});
		button9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "9");
			}
		});
		button0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText(display.getText() + "0");
			}
		});
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				display.setText("");
			}
		});
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae1) {
				// TODO generate Event
			}
		});
		coin1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae1) {
				// TODO action for coin1
			}
		});
		coin2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae1) {
				// TODO action for coin2
			}
		});
		coin3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae1) {
				// TODO action for coin3
			}
		});
		coin4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae1) {
				// TODO action for coin4
			}
		});

	}
}