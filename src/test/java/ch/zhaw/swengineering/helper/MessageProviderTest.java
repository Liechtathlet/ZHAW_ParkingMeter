package ch.zhaw.swengineering.helper;

import ch.zhaw.swengineering.model.Message;
import ch.zhaw.swengineering.model.Messages;
import ch.zhaw.swengineering.setup.ParkingMeterRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=ParkingMeterRunner.class, loader=AnnotationConfigContextLoader.class)
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

		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(result, value);
	}

	@Test
	public void gettingDefaultMessageWhenKeyIsNotPresent() throws Exception {
		String key = "Bumblebee";
		Messages messages = new Messages();
		messages.messages = new ArrayList<Message>();

		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(messages);

		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(result, String.format("Key %s not found", key));
	}

	@Test
	public void gettingDefaultMessageWhenConfigurationProviderReturnsNull() throws Exception {
		String key = "Bumblebee";

		MockitoAnnotations.initMocks(this);
		when(configurationProvider.get()).thenReturn(null);

		// Run
		String result = messageProvider.get(key);

		// Assert
		assertEquals(result, String.format("Key %s not found", key));
	}
}