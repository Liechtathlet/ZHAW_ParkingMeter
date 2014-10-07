package ch.zhaw.swengineering;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import ch.zhaw.swengineering.model.Greeting;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
public class RestServiceTest {

	private MockMvc mockMvc;

	@Autowired
	private Greeting greetingMock;

	//Add WebApplicationContext field here.

	//The setUp() method is omitted.

	@Test
	@Ignore
	public void findAll_TodosFound_ShouldReturnFoundTodoEntries() throws Exception {

		when(greetingMock.getContent()).thenReturn("mock text");

		mockMvc.perform(get("/greeting"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.content", is("mock text")));

		verify(greetingMock, times(1));
		verifyNoMoreInteractions(greetingMock);
	}
}
