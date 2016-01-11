package urlshortener2015.imperialred.web;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
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
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.DBObject;

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.Alert;
import urlshortener2015.imperialred.objects.Click;
import urlshortener2015.imperialred.objects.Ip;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.objects.Synonym;
import urlshortener2015.imperialred.objects.WebSocketsData;
import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.AlertRepository;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.IpRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;
import urlshortener2015.imperialred.repository.UserRepository;

@Controller
public class UrlShortenerControllerWithLogs {

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	protected ShortURLRepository shortURLRepository;

	@Autowired
	protected IpRepository ipRepository;

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected AlertRepository alertRepository;

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

	@RequestMapping(value = "/{id:(?!link|index|profile).*}", method = RequestMethod.GET)
	public Object redirectTo(@PathVariable String id,
			@RequestParam(value = "token", required = false) String token,
			HttpServletResponse response, HttpServletRequest request,
			Model model) {

		logger.info("Requested redirection with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		logger.info("su: " + l);
		logger.info(l == null ? "null" : "not null");
		if (l != null) {
			/*
			 * Check Token
			 */
			if (l.getToken() != null
					&& (token == null || !l.getToken().equals(token))) {
				/*
				 * Wrong Token
				 */
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				throw new CustomException("400", "It is need a token");
			}
			else {

				Date d = new Date(System.currentTimeMillis());
				if (l.getExpire() != null && d.after(l.getExpire())) {
					/*
					 * Date has expired
					 */
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					throw new CustomException("400", "Link has expired");

				}
				else {
					ArrayList<String> rules = l.getRules();
					if (rules != null && !rules.isEmpty()) {
						/*
						 * Execute javascript
						 */
						for (int i = 0; i < rules.size(); i++) {
							Boolean resul = executeS(rules.get(i), l);
							if (resul != null) {
								if (resul == true) {
									response.setStatus(
											HttpStatus.BAD_REQUEST.value());
									throw new CustomException("400",
											"Link has expired");
								}
							}
							else {
								response.setStatus(
										HttpStatus.BAD_REQUEST.value());
								throw new CustomException("400", "Bad rule");
							}
						}
					}

					List<String> authorizedMails = l.getAllowedUsers();
					if (authorizedMails != null && !authorizedMails.isEmpty()) {

						if (!authentication(authorizedMails)) {
							request.getSession().setAttribute("redirect", id);

							// model.addAttribute("hash", id);
							return "login";
						}

					}
					createAndSaveClick(id, request);
					long click = clickRepository.clicksByHash(l.getHash(), null,
							null, null, null, null, null);
					/* Data from countries */
					DBObject groupObject = clickRepository
							.getClicksByCountry(id, null, null).getRawResults();
					String list = groupObject.get("retval").toString();
					String countryData = StatsController
							.processCountryJSON(list);
					/* Data from cities */
					DBObject groupObjectCity = clickRepository
							.getClicksByCity(id, null, null, null, null, null,
									null)
							.getRawResults();
					String listCities = groupObjectCity.get("retval")
							.toString();
					String cityData = StatsController
							.processCityJSON(listCities);
					WebSocketsData wb = new WebSocketsData(false, click,
							countryData, cityData);
					this.template.convertAndSend("/topic/" + id, wb);
					return createSuccessfulRedirectToResponse(l);
				}
			}
		}
		else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			throw new CustomException("400",
					"BAD_REQUEST\nURL SHORTENED DOESN'T EXISTS");
		}
	}

	private boolean authentication(List<String> authorizedMails) {
		String email = getOwnerMail();
		if (email != null) {
			if (!authorizedMails.contains(email)) {
				/*
				 * Not authorized
				 */
				throw new CustomException("401",
						"You don't have permissions to use this URL");
			}
			/*
			 * 																	
			 */
			return true;
		}
		else {
			/*
			 * Not logged
			 */
			return false;
		}

	}

	@RequestMapping(value = "/link", method = RequestMethod.POST)
	public ResponseEntity<?> shortener(@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken,
			@RequestParam(value = "emails[]", required = false) String[] emails,
			@RequestParam(value = "days", required = false) String days,
			HttpServletRequest request, Principal principal) throws Exception {
		JSONObject jn = getFreegeoip(request);
		String country = jn.getString("country_name");
		ShortURL su = createAndSaveIfValid(url, custom, hasToken, expireDate,
				extractIP(request), emails, principal, country);
		if (su != null) {
			/* If there is an expire date, it sets an alert */
			if (!expireDate.equals("")) {
				Date alertDate = processAlertDate(expireDate, days);
				logger.info("New alert date: " + alertDate);
				String mail = UrlShortenerControllerWithLogs.getOwnerMail();
				Alert alert = new Alert(mail, su.getHash(), alertDate);
				alertRepository.save(alert);
			}
			HttpHeaders h = new HttpHeaders();
			h.setLocation(su.getUri());
			return new ResponseEntity<>(su, h, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Changes the expire date and alert for the new specified
	 */
	@RequestMapping(value = "/changeExpire", method = RequestMethod.GET)
	public ResponseEntity<?> changeExpireDate(HttpServletRequest request,
			@RequestParam(value = "url", required = false) String hash,
			@RequestParam(value = "expire", required = false) String expire,
			@RequestParam(value = "days", required = false) String days) {
		hash = hash.substring(1, hash.length() - 1);

		/* Changes expire date */
		ShortURL su = shortURLRepository.findByHash(hash);
		logger.info("su: " + su);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date newExpire = null;
		try {
			newExpire = sdf.parse(expire);
		}
		catch (ParseException e) {
			e.printStackTrace();
			logger.info("Error with introduced alert date");
		}
		su.setExpire(newExpire);
		logger.info("Updating ShortURL: " + su);
		shortURLRepository.save(su);

		/* Changes alert date */
		Alert a = alertRepository.findByHash(hash);
		Date alertDate = processAlertDate(expire, days);
		if (a != null) {
			/* If alert already exists, updates its alert date */
			a.setDate(alertDate);
			logger.info("Updating alert: " + a);
			alertRepository.save(a);
		}
		else {
			/* If alert does not exist, creates a new one */
			String mail = UrlShortenerControllerWithLogs.getOwnerMail();
			logger.info("Setting new alert");
			alertRepository.save(new Alert(mail, hash, alertDate));
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Given an expire date for a link and the previous days for the alert to be
	 * sent, calculates the date in which the alert will be sent. Since the user
	 * does not specify a time, it is set to 00:00.
	 */
	protected Date processAlertDate(String expireDate, String days) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate expireLocal = LocalDate.parse(expireDate, formatter);
		LocalDate alertLocal = expireLocal.minusDays(Long.parseLong(days));
		return Date.from(
				alertLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	@RequestMapping(value = "/rec/rec", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<String>> recomendaciones(
			@RequestParam("url") String url,
			@RequestParam(value = "custom", required = false) String custom,
			@RequestParam(value = "expire", required = false) String expireDate,
			@RequestParam(value = "hasToken", required = false) String hasToken,
			HttpServletRequest request) {

		UrlValidator urlValidator = new UrlValidator(
				new String[] { "http", "https" });
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
						Synonym sin = map.readValue(
								response.getBody().toString(), Synonym.class);
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
		BigInteger valueIp = getIpValue(dirIp);
		Ip subnet = ipRepository.findSubnet(valueIp);
		String country = (subnet != null) ? (subnet.getCountry()) : ("");

		request.getHeader(USER_AGENT);
		JSONObject jn = getFreegeoip(request);
		String city = jn.getString("city");
		Float latitude = new Float(jn.getDouble("latitude"));
		Float longitude = new Float(jn.getDouble("longitude"));
		Click cl = new Click(null, hash, new Date(System.currentTimeMillis()),
				request.getHeader(REFERER), request.getHeader(USER_AGENT),
				request.getHeader(USER_AGENT), dirIp, country, city, longitude,
				latitude);
		cl = clickRepository.save(cl);
		logger.info(
				cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]"
						: "[" + hash + "] was not saved");
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
	 * If shortURL is valid, creates it and saves it 
	 * XXX: at the moment, it just ignores unknown emails, with no feedback for users.
	 */
	protected ShortURL createAndSaveIfValid(String url, String custom,
			String hasToken, String expireDate, String ip, String[] emails,
			Principal principal, String country) {

		UrlValidator urlValidator = new UrlValidator(
				new String[] { "http", "https" });
		/*
		 * Check if url comes through http or https
		 */
		if (urlValidator.isValid(url)) {
			logger.info("Shortening valid url " + url);
			/*
			 * Creates a hash from the current date in case this is not custom
			 */
			String id;
			String now = new Date().toString();
			if (custom.equals("")) {
				id = Hashing.murmur3_32()
						.hashString(now, StandardCharsets.UTF_8).toString();
			}
			else {
				id = custom;
			}
			
			/*
			 * Has Token
			 */
			String token = null;
			if (hasToken != null && !hasToken.equals("")) {
				token = UUID.randomUUID().toString();
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
					logger.info("Error: badly introduced date.");
				}
			}
			
			/*
			 * Checks every mail inserted by the user, and maintains a list with
			 * those corresponding to registered users.
			 */
			List<String> trueEmails = new ArrayList<String>();
			for (int i = 0; i < emails.length; i++) {
				if (!emails[i].equals("")) {
					User foundUser = null;

					foundUser = userRepository.findByMail(emails[i]);
					if (foundUser != null) {
						trueEmails.add(foundUser.getMail());
					}

				}
			}
			
			/*
			 * If no valid emails are introduced, link will be public and it
			 * wont have an email list.
			 */
			boolean isPrivate = false;
			if (trueEmails.size() > 0) {
				isPrivate = true;
			}
			else {
				trueEmails = null;
			}
			
			/*
			 * Gets email
			 */
			String owner = getOwnerMail();
			
			/*
			 * Creates ShortURL object
			 */
			ShortURL su = new ShortURL(id, url,
					linkTo(methodOn(UrlShortenerControllerWithLogs.class)
							.redirectTo(id, null, null, null, null)).toUri(),
					new Date(System.currentTimeMillis()), expire, owner, token,
					HttpStatus.TEMPORARY_REDIRECT.value(), ip, country,
					isPrivate, trueEmails);

			/*
			 * Insert to DB
			 */
			return shortURLRepository.save(su);
		}
		else {
			logger.info("Not valid url " + url);
			return null;
		}
	}

	protected String extractIP(HttpServletRequest request) {

		String dirIp = request.getRemoteAddr();
		if (dirIp.equals("0:0:0:0:0:0:0:1") || dirIp.equals("127.0.0.1")) {
			dirIp = "79.159.252.76";
		}
		return dirIp;

	}

	protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
		HttpHeaders h = new HttpHeaders();
		h.setLocation(URI.create(l.getTarget()));
		System.out.println(l.getMode());
		return new ResponseEntity<>(l, h, HttpStatus.valueOf(l.getMode()));
	}

	private JSONObject getFreegeoip(HttpServletRequest request) {
		try {
			String dirIp = extractIP(request);

			HttpResponse<JsonNode> response = Unirest
					.get("http://freegeoip.net/json/" + dirIp).asJson();
			return response.getBody().getObject();

		}
		catch (UnirestException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static String getOwnerMail() {
		Authentication currentAuthentication = SecurityContextHolder
				.getContext().getAuthentication();
		String email = "";
		if (currentAuthentication instanceof SocialAuthenticationToken) {
			SocialAuthenticationToken social = (SocialAuthenticationToken) currentAuthentication;

			switch (social.getProviderId()) {
			case ("google"):
				Google google = (Google) social.getConnection().getApi();
				email = google.plusOperations().getGoogleProfile()
						.getAccountEmail();
				break;
			case ("facebook"):
				Facebook facebook = (Facebook) social.getConnection().getApi();
				email = facebook.userOperations().getUserProfile().getEmail();
				break;
			}
		}
		else if (currentAuthentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken local = (UsernamePasswordAuthenticationToken) currentAuthentication;
			email = (String) local.getPrincipal();
		}
		else if (currentAuthentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return email;
	}

	/**
	 * Execute the rule and return true or false.
	 * @param rules
	 * @param l
	 * @return
	 */
	public Boolean executeS(String rule, ShortURL l) {
		try {
			if (rule.contains("<")) {
				String[] partes = rule.split("<");
				if (partes[0].equals("created")) {
					if (l.getCreated().before(new SimpleDateFormat("yyyy-MM-dd")
							.parse(partes[1]))) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("expire")) {
					if (l.getExpire().before(new SimpleDateFormat("yyyy-MM-dd")
							.parse(partes[1]))) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("token")) {
					return null;
				}
				else if (partes[0].equals("country")) {

					return null;
				}
				else if (partes[0].equals("clicks")) {
					if (clickRepository.clicksByHash(l.getHash(), null, null,
							null, null, null, null) < Long.valueOf(partes[1])) {
						return true;
					}
					else {
						return false;
					}
				}
				return null;
			}
			else if (rule.contains(">")) {
				String[] partes = rule.split(">");
				if (partes[0].equals("created")) {
					if (l.getCreated().after(new SimpleDateFormat("yyyy-MM-dd")
							.parse(partes[1]))) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("expire")) {
					if (l.getExpire().after(new SimpleDateFormat("yyyy-MM-dd")
							.parse(partes[1]))) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("token")) {
					return null;
				}
				else if (partes[0].equals("country")) {

					return null;
				}
				else if (partes[0].equals("clicks")) {
					if (clickRepository.clicksByHash(l.getHash(), null, null,
							null, null, null, null) > Long.valueOf(partes[1])) {
						return true;
					}
					else {
						return false;
					}
				}
				return null;
			}
			else if (rule.contains("==")) {
				String[] partes = rule.split("==");
				if (partes[0].equals("created")) {
					if (l.getCreated()
							.compareTo((new SimpleDateFormat("yyyy-MM-dd")
									.parse(partes[1]))) == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("expire")) {
					if (l.getExpire()
							.compareTo((new SimpleDateFormat("yyyy-MM-dd")
									.parse(partes[1]))) == 0) {
						return true;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("token")) {
					if (partes[1].equals("true")) {
						return l.getToken() != null;
					}
					else if (partes[1].equals("false")) {
						return l.getToken() == null;
					}
					else {
						return false;
					}
				}
				else if (partes[0].equals("country")) {

					return l.getCountry().equals(partes[1]);
				}
				else if (partes[0].equals("clicks")) {
					if (clickRepository.clicksByHash(l.getHash(), null, null,
							null, null, null,
							null) == Long.valueOf(partes[1])) {
						return true;
					}
					else {
						return false;
					}
				}
				return null;

			}

			else {
				return null;
			}
		}
		catch (Exception e) {
			return null;
		}

	}
}
