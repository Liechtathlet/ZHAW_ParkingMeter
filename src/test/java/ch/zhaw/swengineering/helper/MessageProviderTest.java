package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.persistence.Message;
import ch.zhaw.swengineering.model.persistence.Messages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageProviderTest {

	@InjectMocks
	private MessageProvider messageProvider;

	@Mock
	private ConfigurationProvider configurationProvider;

	@Test
	public void gettingBarWhenAskingForFoo() throws Exception {
		// Mock
		String key = "foo";
		String value = "bar";

		final Message message = new Message();
		message.key = key;
		message.value = value;

		Messages messages = new Messages();
		messages.messages = new ArrayList<Message>() {{ add(message); }};

		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(messages);

		messageProvider.init();
		
		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(result, value);
	}

	@Test
	public void gettingDefaultMessageWhenKeyIsNotPresent() throws Exception {
		String key = "Bumblebee";
		Messages messages = new Messages();
		messages.messages = new ArrayList<>();

		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(messages);

		messageProvider.init();
		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(key, result);
	}

	@Test
	public void gettingDefaultMessageWhenConfigurationProviderReturnsNull() throws Exception {
		String key = "Bumblebee";

		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(null);

		messageProvider.init();
		
		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(key,result);
	}
}