package ch.zhaw.swengineering.controller;

import org.springframework.stereotype.Controller;

import ch.zhaw.swengineering.model.SecretCode;

/**
 * @author Bernhard Fuchs Interface for SecretCode Controllers Provides methods
 *         to access the secret code information.
 */
@Controller
public interface SecretCodeController {

    /**
     * Gets the information about the secret code.
     * @param aNumber
     *            the secret code number.
     * @return the information corresponding to the secret code or null, if no
     *         matching secret code could be found.
     */
    SecretCode getSecretCode(int aNumber);
}
