package urlshortener2015.imperialred.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONArray;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.Click;
import urlshortener2015.imperialred.objects.Ip;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.objects.Synonym;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.IpRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;

@RestController
public class UrlShortenerControllerWithLogs {

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected IpRepository ipRepository;

	/**
	 * The HTTP {@code Referer} header field name.
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.2">Section
	 *      5.5.2 of RFC 7231</a>
	 */
	public static final String REFERER = "Referer";

	/**
	 * The HTTP {@code User-Agent} header field name.
	 * 
	 * @see <a href="http://tools.ietf.org/html/rfc7231#section-5.5.3">Section
	 *      5.5.3 of RFC 7231</a>
	 */
	public static final String USER_AGENT = "User-Agent";

	private static final Logger logger = LoggerFactory
			.getLogger(UrlShortenerControllerWithLogs.class);

	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			@RequestParam(value = "token", required = false) String token,HttpServletResponse response,
			HttpServletRequest request) {

		logger.info("Requested redirection with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		if (l != null) {
			/*
			 * Check Token
			 */
			if (l.getOwner() != null
					&& (token == null || !l.getOwner().equals(token))) {
				/*
				 * Wrong Token
				 */
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				throw new CustomException("400", "Se necesita un token");
			}
			else {

				Date d = new Date(System.currentTimeMillis());
				if (l.getExpire() != null && d.after(l.getExpire())) {
					/*
					 * Date has expired
					 */
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					throw new CustomException("400", "Enlace ha expirado");

				}
				else {
					createAndSaveClick(id, request);
					return createSuccessfulRedirectToResponse(l);
				}
			}
		}
		else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			throw new CustomException("400", "BAD_REQUEST");
		}
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(
			@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken,
			HttpServletRequest request) throws Exception {
		ShortURL su = createAndSaveIfValid(url, custom, hasToken, expireDate,
				extractIP(request));
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/rec/rec", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<String>> recomendaciones(
			@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken,
			HttpServletRequest request) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		/*
		 * Check if url comes through http or https
		 */
		if (urlValidator.isValid(url)) {
			/*
			 * Hash of URL or custom
			 */
			String id;
			if (!custom.equals("")) {

				id = custom;

				if (shortURLRepository.findByHash(id) == null) {
					System.out.println("1");
					return new ResponseEntity<>(HttpStatus.CREATED);
				}
				else {
					System.out.println("2");
					try {
						HttpResponse<JsonNode> response = Unirest
								.get("https://wordsapiv1.p.mashape.com/words/"
										+ id + "/synonyms")
								.header("X-Mashape-Key",
										"VLzNEVr9zQmsh0gOlqs6wudMxDo1p1vCnjEjsnjNBhOCFeqLxr")
								.header("Accept", "application/json").asJson();
						ObjectMapper map = new ObjectMapper();
						Synonym sin = map.readValue(response.getBody()
								.toString(), Synonym.class);
						return new ResponseEntity<>(sin.getSynonyms(),
								HttpStatus.BAD_REQUEST);
					}
					catch (Exception e) {
						/*
						 * Caso en el que la id seleccionada esta cogida y la
						 * API no da alternativas
						 */
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					}
				}
			}
			else {
				return new ResponseEntity<>(HttpStatus.OK);
			}
		}
		else {
			System.out.println("3");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	protected void createAndSaveClick(String hash, HttpServletRequest request) {
		/* Gets the IP from the request, and looks in the db for its country */
		String dirIp = extractIP(request);
		if(dirIp.equals("127.0.0.1"))dirIp="52.29.234.196";
		BigInteger valueIp = getIpValue(dirIp);
		Ip subnet = ipRepository.findSubnet(valueIp);
		String country = (subnet != null) ? (subnet.getCountry()) : ("");

		request.getHeader(USER_AGENT);
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				request.getHeader(REFERER), request.getHeader(USER_AGENT),
				request.getHeader(USER_AGENT), dirIp, country);
		cl = clickRepository.save(cl);
		logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId()
				+ "]" : "[" + hash + "] was not saved");
	}

	/**
	 * Given an IP string, returns the corresponding number of that IP
	 */
	private BigInteger getIpValue(String dirIp) {
		if (dirIp.contains(".")) {
			String[] parts = dirIp.split("\\.");
			long num = Long.parseLong(parts[0]) * 16777216
					+ Long.parseLong(parts[1]) * 65536
					+ Long.parseLong(parts[2]) * 256 + Long.parseLong(parts[3]);
			return BigInteger.valueOf(num);

		}
		else {
			/* Still not implemented for IPv6 */
			return BigInteger.valueOf(-1);
		}
	}

	/**
	 * Creacion y guardado de la URL
	 */
	protected ShortURL createAndSaveIfValid(String url, String custom,
			String hasToken, String expireDate, String ip) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		/*
		 * Check if url comes through http or https
		 */
		if (urlValidator.isValid(url)) {
			/*
			 * Hash of URL or custom
			 */
			String id;
			if (custom.equals("")) {
				id = Hashing.murmur3_32()
						.hashString(url, StandardCharsets.UTF_8).toString();
			}
			else {
				id = custom;
			}

			/*
			 * If exists, it isnt created
			 */
			// if (shortURLRepository.findByHash(id) == null) {

			/*
			 * Has Token
			 */
			String owner = null;
			if (hasToken != null && !hasToken.equals("")) {
				owner = UUID.randomUUID().toString();
			}
			/*
			 * Expire date
			 */
			Date expire = null;
			if (!expireDate.equals("")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				try {
					expire = sdf.parse(expireDate);
				}
				catch (ParseException e) {
					e.printStackTrace();
					logger.info("Fecha mal introducida");
				}
			}

			/*
			 * Creacion del objeto ShortURL
			 */

			ShortURL su = new ShortURL(id, url, linkTo(
					methodOn(UrlShortenerControllerWithLogs.class).redirectTo(
							id, null, null,null)).toUri(), new Date(
					System.currentTimeMillis()), expire, owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), ip, null);
			/*
			 * Insert to DB
			 */
			return shortURLRepository.save(su);

			/*
			 * } else { return null; }
			 */
		}
		else {
			return null;
		}
	}

	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		System.out.println(l.getMode());
		return new ResponseEntity<>(l,h, HttpStatus.valueOf(l.getMode()));
	}

}
