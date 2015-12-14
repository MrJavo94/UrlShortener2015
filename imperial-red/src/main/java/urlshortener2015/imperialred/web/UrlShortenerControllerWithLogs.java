package urlshortener2015.imperialred.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	/**
	 * 
	 */
	public ResponseEntity<?> redirectTo(@PathVariable String id,
			@RequestParam(value = "token", required = false) String token, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		if (l != null) {
			/*
			 * Comprobar Token
			 */
			if (l.getOwner() != null && (token == null || !l.getOwner().equals(token))) {
				/*
				 * Token incorrecto
				 */
				throw new CustomException("400", "Se necesita un token");
			} else {

				Date d = new Date(System.currentTimeMillis());
				if (l.getExpire() != null && d.after(l.getExpire())) {
					/*
					 * La fecha ha expirado
					 */
					throw new CustomException("400", "Enlace ha expirado");

				} else {
					createAndSaveClick(id, request);
					return createSuccessfulRedirectToResponse(l);
				}
			}
		} else {
			throw new CustomException("404", "NOT_FOUND");
		}
	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken, HttpServletRequest request)
					throws Exception {
		ShortURL su = createAndSaveIfValid(url, custom, hasToken, expireDate, extractIP(request));
		if (su != null) {
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/rec/rec", method = RequestMethod.GET)
	public ResponseEntity<Synonym> recomendaciones(@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken, HttpServletRequest request){

		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		/*
		 * Comprueba si la url viene con http o https
		 */
		if (urlValidator.isValid(url)) {
			/*
			 * Hasheado de la URL o personalizado
			 */
			String id;
			if (custom.equals("")) {
				id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
			} else {
				id = custom;
			}

			if (shortURLRepository.findByHash(id) == null) {
				System.out.println("1");
				return new ResponseEntity<>(HttpStatus.CREATED);
			} else {
				System.out.println("2");
				try{
					HttpResponse<JsonNode> response = Unirest.get("https://wordsapiv1.p.mashape.com/words/" + id + "/synonyms")
					.header("X-Mashape-Key", "VLzNEVr9zQmsh0gOlqs6wudMxDo1p1vCnjEjsnjNBhOCFeqLxr")
					.header("Accept", "application/json")
					.asJson();
					ObjectMapper map= new ObjectMapper();					
					Synonym sin = map.readValue(response.getBody().toString(), Synonym.class);
					return new ResponseEntity<>(sin,HttpStatus.BAD_REQUEST);
				} catch(Exception e){
					/*
					 * Caso en el que la id seleccionada esta cogida y la API
					 * no da alternativas
					 */
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);				
				}
			}
		} else {
			System.out.println("3");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	protected void createAndSaveClick(String hash, HttpServletRequest request) {
		/* Gets the IP from the request, and looks in the db for its country */
		String dirIp = extractIP(request);
		BigInteger valueIp = getIpValue(dirIp);
		Ip subnet = ipRepository.findSubnet(valueIp);
		String country = (subnet != null) ? (subnet.getCountry()) : ("");

		request.getHeader(USER_AGENT);
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), request.getHeader(REFERER),
				request.getHeader(USER_AGENT), request.getHeader(USER_AGENT), dirIp, country);
		cl = clickRepository.save(cl);
		logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
	}

	/**
	 * Given an IP string, returns the corresponding number of that IP
	 */
	private BigInteger getIpValue(String dirIp) {
		if (dirIp.contains(".")) {
			String[] parts = dirIp.split("\\.");
			long num = Long.parseLong(parts[0]) * 16777216 + Long.parseLong(parts[1]) * 65536
					+ Long.parseLong(parts[2]) * 256 + Long.parseLong(parts[3]);
			return BigInteger.valueOf(num);

		} else {
			/* Still not implemented for IPv6 */
			return BigInteger.valueOf(-1);
		}
	}

	/**
	 * Creacion y guardado de la URL
	 */
	protected ShortURL createAndSaveIfValid(String url, String custom, String hasToken, String expireDate, String ip) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		/*
		 * Comprueba si la url viene con http o https
		 */
		if (urlValidator.isValid(url)) {
			/*
			 * Hasheado de la URL o personalizado
			 */
			String id;
			if (custom.equals("")) {
				id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
			} else {
				id = custom;
			}

			/*
			 * Si ya existe no se crea, de momento
			 */
			if (shortURLRepository.findByHash(id) == null) {

				/*
				 * Has Token
				 */
				String owner = null;
				if (hasToken != null) {
					owner = UUID.randomUUID().toString();
				}
				/*
				 * Fecha de expiracion
				 */
				Date expire = null;
				if (!expireDate.equals("")) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

					try {
						expire = sdf.parse(expireDate);
					} catch (ParseException e) {
						e.printStackTrace();
						logger.info("Fecha mal introducida");
					}
				}
				/*
				 * Creacion del objeto ShortURL
				 */
				ShortURL su = new ShortURL(id, url,
						linkTo(methodOn(UrlShortenerControllerWithLogs.class).redirectTo(id, null, null)).toUri(),
						new Date(System.currentTimeMillis()), expire, owner, HttpStatus.TEMPORARY_REDIRECT.value(), ip,
						null);
				/*
				 * Insercion en la base de datos
				 */
				return shortURLRepository.save(su);

			} else {
				return null;
			}
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
