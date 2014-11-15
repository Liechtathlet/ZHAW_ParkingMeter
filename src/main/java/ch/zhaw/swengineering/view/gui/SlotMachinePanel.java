package ch.zhaw.swengineering.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.zhaw.swengineering.slotmachine.controller.IntelligentSlotMachineUserInteractionInterface;
import ch.zhaw.swengineering.view.gui.listeners.CoinInputActionListener;

/**
 * @author Daniel Brun
 * 
 *         Panel to display the interface of the slot machine.
 */
public class SlotMachinePanel extends JPanel implements ActionListener {

    /**
     * GUID.
     */
    private static final long serialVersionUID = -64522089814962535L;

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(SlotMachinePanel.class);

    /**
     * Static color-definitions.
     */
    private static final Color BG = new Color(93, 92, 102);
    private static final Color BG_INNER = new Color(26, 51, 115);
    private static final Color BG_SLUT = new Color(119, 136, 153);

    private int initialHeight;
    private int initialWidth;

    private JPanel slotMachineBorderPanel;
    private JPanel slotMachineInnerPanel;
    private JPanel coinButtonPanel;
    private JPanel slotPanel;

    private List<JButton> coinButtonList;
    private CoinInputActionListener coinInputListener;

    private JTextArea coinSlot = new JTextArea("");

    private IntelligentSlotMachineUserInteractionInterface slotMachine;

    /**
     * Creates a new parking meter panel.
     * 
     * @param aSlotMachine
     *            The slot machine.
     */
    public SlotMachinePanel(
            IntelligentSlotMachineUserInteractionInterface aSlotMachine) {

        this.slotMachine = aSlotMachine;
        initialHeight = 300;
        initialWidth = 100;

        coinButtonList = new ArrayList<JButton>();

        coinInputListener = new CoinInputActionListener(null);

        slotMachineBorderPanel = new JPanel();
        slotMachineInnerPanel = new JPanel();
        coinButtonPanel = new JPanel();
        slotPanel = new JPanel();

        slotMachineBorderPanel.setBackground(BG_INNER);
        slotMachineBorderPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10,
                10));

        slotMachineInnerPanel.setLayout(new BorderLayout());
        slotMachineInnerPanel.setBackground(BG_INNER);
        slotMachineInnerPanel.setPreferredSize(new Dimension(initialWidth,
                520));

        coinButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        coinButtonPanel.setBackground(BG_INNER);
        coinButtonPanel.setPreferredSize(new Dimension(initialWidth,
                slotMachine.getAvailableCoins().size() * 50));

        slotPanel.setBackground(BG_INNER);
        slotPanel.setPreferredSize(new Dimension(initialWidth, 50));

        // Create coin buttons
        for (BigDecimal coinValue : slotMachine.getAvailableCoins()) {
            JButton coinBtn = new JButton(coinValue.toString());
            // TODO: evtl. hash map with mapping.
            coinBtn.addActionListener(coinInputListener);
            coinButtonList.add(coinBtn);
            coinButtonPanel.add(coinBtn);
        }

        coinSlot.setSize(10, 40);
        coinSlot.setColumns(1);
        coinSlot.setRows(4);
        coinSlot.setEditable(false);
        coinSlot.setBackground(BG_SLUT);
        slotPanel.add(coinSlot);

        slotMachineInnerPanel.add(coinButtonPanel, BorderLayout.NORTH);
        slotMachineInnerPanel.add(slotPanel, BorderLayout.CENTER);

        slotMachineBorderPanel.add(slotMachineInnerPanel);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
        this.setBackground(BG);
        this.add(slotMachineBorderPanel);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
