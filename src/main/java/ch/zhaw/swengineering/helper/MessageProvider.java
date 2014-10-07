package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.Message;
import ch.zhaw.swengineering.model.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MessageProvider {

	@Autowired
	@Qualifier("messages-de")
	private ConfigurationProvider configurationProvider;

	public String get(String key) {
		if (configurationProvider != null && configurationProvider.get() != null) {
			// TODO: Casting should be done only once. But it does not work in the
			// constructor because the poperty will not be injected.
			Messages messages = (Messages) configurationProvider.get();
			for (Message message : messages.messages) {
				if (message.key.equals(key)) {
					return message.value;
				}
			}
		}
		return String.format("Key %s not found", key);
	}
}
