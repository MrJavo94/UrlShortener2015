package urlshortener2015.imperialred.web;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener2015.imperialred.web.fixture.ShortURLFixture.someUrl;
import static urlshortener2015.imperialred.repository.fixture.ClickFixture.click;
import static urlshortener2015.imperialred.repository.fixture.ShortURLFixture.url1;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener2015.imperialred.objects.Click;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;

public class UrlShortenerTests {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRepository;

	@InjectMocks
	private UrlShortenerControllerWithLogs urlShortener;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
	}

	/*@Test
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
			throws Exception {
		configureTransparentSaveClick();
		when(shortURLRepository.findByHash("someKey")).thenReturn(someUrl());

		mockMvc.perform(get("/{id}", "someKey")).andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://example.com/"));
	}*/
	
	@Test
	public void thatShortenerFailsIfTheURLisWrong() throws Exception {
		configureTransparentSave();

		mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
				.andExpect(status().isBadRequest());
	}

	//@Test
	public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
		configureTransparentSave();
		
		mockMvc.perform(
				post("/link").param("url", "http://example.com/")
						.param("custom", "hola").param("expire", "2016-11-15")
						.param("hasToken", "true").param("emails[]", "")
						.param("alert_email", "example@example.com").param("days", "1"))
				.andDo(print())
				.andExpect(redirectedUrl("http://localhost/hola"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.hash", is("hola")))
				.andExpect(jsonPath("$.uri", is("http://localhost/hola")))
				.andExpect(jsonPath("$.target", is("http://example.com/")));
	}
	
	/*@Test
    public void thatSavePersistsTheClickURL() {
		configureTransparentSaveClick();
        //Saves the test Click
        clickRepository.save(click(url1()));

        int count = (int) clickRepository.count();

        assertEquals(count,1);
        
        

    }*/

	private void configureTransparentSave() {
		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.then(new Answer<ShortURL>() {
					@Override
					public ShortURL answer(InvocationOnMock invocation)
							throws Throwable {
						return (ShortURL) invocation.getArguments()[0];
					}
				});
	}
	
	private void configureTransparentSaveClick() {
		when(clickRepository.save(org.mockito.Matchers.any(Click.class)))
				.then(new Answer<Click>() {
					@Override
					public Click answer(InvocationOnMock invocation)
							throws Throwable {
						return (Click) invocation.getArguments()[0];
					}
				});
	}

}