package ch.zhaw.swengineering.helper;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ch.zhaw.swengineering.model.persistence.Message;
import ch.zhaw.swengineering.model.persistence.Messages;

@Component
public class MessageProvider {

    /**
     * The Logger.
     */
    private static final Logger LOG = LogManager
            .getLogger(MessageProvider.class);

    @Autowired
    @Qualifier("messages-de")
    private ConfigurationProvider configurationProvider;

    private HashMap<String, String> messageMap;

    /**
     * Loads the necessary data.
     */
    @PostConstruct
    public final void init() {
        messageMap = new HashMap<String, String>();

        if (configurationProvider != null
                && configurationProvider.get() != null) {
            Messages messages = (Messages) configurationProvider.get();

            for (Message message : messages.messages) {
                messageMap.put(message.key, message.value);
            }
        } else {
            LOG.error("Could not load messages via ConfigurationProvider!");
        }
    }

    /**
     * Gets the message to the given key.
     * 
     * @param key
     *            the key of the message.
     * @return the value of the message or the key, if no translation could be found.
     */
    public String get(final String key) {
        if (messageMap.containsKey(key)) {
            return messageMap.get(key);
        } else {
            LOG.warn("Translation for key '" + key + "' not found!");
            return key;
        }
    }
}
