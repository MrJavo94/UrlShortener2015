package urlshortener2015.imperialred.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

import urlshortener2015.imperialred.objects.Click;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.repository.ClickRepository;

@RestController
public class UrlShortenerControllerWithLogs{
	
	
	@Autowired 
	protected ClickRepository clickRepository;
	
	/**
	 * The HTTP {@code Referer} header field name.
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.2">Section 5.5.2 of RFC 7231</a>
	 */
	public static final String REFERER = "Referer";
	
	/**
	 * The HTTP {@code User-Agent} header field name.
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.3">Section 5.5.3 of RFC 7231</a>
	 */
	public static final String USER_AGENT = "User-Agent";

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	/**
	 * 
	 */
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		//ShortURL l = shortURLRepository.findByKey(id);
		ShortURL l = null;
		if (l != null) {
			createAndSaveClick(id, request);
			return createSuccessfulRedirectToResponse(l);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand, HttpServletRequest request) {
		ShortURL su = createAndSaveIfValid(url, sponsor, brand, UUID.randomUUID().toString(), extractIP(request));
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * 
	 */
	protected void createAndSaveClick(String hash, HttpServletRequest request) {
		/*
		 * 
		 */
		request.getHeader(USER_AGENT);
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), request.getHeader(REFERER), 
				request.getHeader(USER_AGENT), request.getHeader(USER_AGENT), extractIP(request), null);
		cl = clickRepository.save(cl);
		logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
	}

	/**
	 * Creacion y guardado de la URL
	 */
	protected ShortURL createAndSaveIfValid(String url, String sponsor, String brand, String owner, String ip) {	
		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		/*
		 * Comprueba si la url viene con http o https
		 */
		if (urlValidator.isValid(url)) {
			/*
			 * Hasheado de la URL
			 * Que coño es murmur?
			 */
			String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
			/*
			 * Creacion del objeto ShortURL
			 */
			ShortURL su = new ShortURL(id, url,
					linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(id, null)).toUri(), sponsor,
					new Date(System.currentTimeMillis()), owner, HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
			/*
			 * Insercion en la base de datos
			 */
			return null;
			//return shortURLRepository.save(su);
		} else {
			return null;
		}
	}
	
	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
	}

}
