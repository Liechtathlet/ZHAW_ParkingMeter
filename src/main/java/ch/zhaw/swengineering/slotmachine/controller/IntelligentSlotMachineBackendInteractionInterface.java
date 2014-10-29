package ch.zhaw.swengineering.slotmachine.controller;

/**
 * @author Daniel Brun
 * 
 *         Interfaces which defines all methods for the interaction betwenn the
 *         intelligent slot machine and the backend.
 */
public interface IntelligentSlotMachineBackendInteractionInterface {

    /**
     * TODO: finish class and method and overthink
     * 
     * @return the amount of money, which was inserted since the last booking.
     */
    double getAmountOfCurrentlyInsertedMoney();

    /**
     * TODO: Finish, should: finish the transaction (Reset values of currently
     * inserted coins and output rest amount).
     */
    void finishTransaction();
}
