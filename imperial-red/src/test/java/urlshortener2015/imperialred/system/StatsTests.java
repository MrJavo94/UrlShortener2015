package urlshortener2015.imperialred.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.containsString;
import urlshortener2015.imperialred.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class StatsTests {
	@Value("${local.server.port}")
	private int port = 0;

	@Test
	public void redirectionToStats() {
		postLink("http://example.com/");
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>(); 
		parts.add("from", null);
		parts.add("to", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4+",
				String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.OK));

	}
	@Test
	public void badRedirectionToStats() {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("from", null);
		parts.add("to", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/4+",
				String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.NOT_FOUND));

	}

	private ResponseEntity<String> postLink(String url) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("url", url);
		parts.add("custom", null);
		parts.add("expire", null);
		parts.add("hasToken", null);
		ResponseEntity<String> entity = new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/link",
				parts, String.class);
		return entity;
	}

}
