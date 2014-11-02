package ch.zhaw.swengineering.event;

import java.util.EventObject;

public class SecretCodeEnteredEvent extends EventObject {

	private final int secretCode;

	public SecretCodeEnteredEvent(Object source, int secretCode) {
		super(source);
		this.secretCode = secretCode;
	}

	public int getSecretCode() {
		return secretCode;
	}
}
