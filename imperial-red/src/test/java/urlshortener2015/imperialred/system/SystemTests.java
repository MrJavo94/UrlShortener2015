package urlshortener2015.imperialred.system;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import urlshortener2015.imperialred.Application;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import java.net.URI;
import java.nio.charset.Charset;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class SystemTests {

	@Value("${local.server.port}")
	private int port = 0;

	@Test
	public void testHome() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port,
				String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.OK));
		assertThat(entity.getHeaders().getContentType(), is(new MediaType("text", "html", Charset.forName("UTF-8"))));
		assertThat(entity.getBody(), containsString("<title>URL"));
	}

	@Test
	public void testCss() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/webjars/bootstrap/3.3.5/css/bootstrap.min.css", String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.OK));
		assertThat(entity.getHeaders().getContentType(), is(MediaType.valueOf("text/css;charset=UTF-8")));
		assertThat(entity.getBody(), containsString("body"));
	}

	@Test
	public void testCreateLink() throws Exception {
		ResponseEntity<String> entity = postLink("http://example.com/", null, null, null);
		assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://localhost:" + this.port + "/f684a3c4")));
		assertThat(entity.getHeaders().getContentType(),
				is(new MediaType("application", "json", Charset.forName("UTF-8"))));
		ReadContext rc = JsonPath.parse(entity.getBody());
		assertThat(rc.read("$.hash"), is("f684a3c4"));
		assertThat(rc.read("$.uri"), is("http://localhost:" + this.port + "/f684a3c4"));
		assertThat(rc.read("$.target"), is("http://example.com/"));

	}

	@Test
	public void testRedirection() throws Exception {
		postLink("http://example.com/", null, null, null);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("token", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4",
				String.class, parts);
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
		assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));

	}

	@Test
	public void testRedirectionCustom() throws Exception {
		postLink("http://example.com/", "hola", null, null);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("token", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/hola",
				String.class, parts);
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
		assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));

	}

	@Test
	public void testRedirectionExpireOK() throws Exception {
		postLink("http://example.com/", null, "2500-05-05", null);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("token", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4",
				String.class, parts);
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
		assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));

	}

	@Test
	public void testRedirectionExpireBAD() throws Exception {
		postLink("http://example.com/", null, "2014-01-01", null);
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("token", null);
		ResponseEntity<?> entity = new TestRestTemplate().getForEntity("http://localhost:" + this.port + "/f684a3c4",
				String.class, parts);
		assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
	}

	@Test
	public void testRedirectionTokenOK() throws Exception {
		ResponseEntity<String> entityPost = postLink("http://example.com/", null, null, "true");
		ReadContext rc = JsonPath.parse(entityPost.getBody());
		String token = rc.read("$.owner");
		ResponseEntity<?> entity = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/f684a3c4?token=" + token, String.class);
		assertThat(entity.getHeaders().getLocation(), is(new URI("http://example.com/")));
		assertThat(entity.getStatusCode(), is(HttpStatus.TEMPORARY_REDIRECT));

	}
	@Test
	public void testRedirectionTokenBAD() throws Exception {
		postLink("http://example.com/", null, null, "true");
		ResponseEntity<?> entity = new TestRestTemplate()
				.getForEntity("http://localhost:" + this.port + "/f684a3c4?token=1", String.class);
		assertThat(entity.getStatusCode(), is(HttpStatus.BAD_REQUEST));

	}

	private ResponseEntity<String> postLink(String url, String custom, String expire, String token) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("url", url);
		parts.add("custom", custom);
		parts.add("expire", expire);
		parts.add("hasToken", token);
		ResponseEntity<String> entity = new TestRestTemplate().postForEntity("http://localhost:" + this.port + "/link",
				parts, String.class);
		return entity;
	}

}