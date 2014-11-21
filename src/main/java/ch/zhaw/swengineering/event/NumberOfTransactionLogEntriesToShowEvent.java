package ch.zhaw.swengineering.event;

import java.util.EventObject;

public class NumberOfTransactionLogEntriesToShowEvent extends EventObject {

    private final int number;

    public NumberOfTransactionLogEntriesToShowEvent(Object source, int number) {
        super(source);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
