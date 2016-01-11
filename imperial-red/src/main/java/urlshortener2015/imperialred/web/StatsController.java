package urlshortener2015.imperialred.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.DBObject;

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.StatsURL;
import urlshortener2015.imperialred.objects.WebSocketsData;
import urlshortener2015.imperialred.objects.Alert;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.repository.AlertRepository;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;

@Controller
public class StatsController {

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	ShortURLRepository shortURLRepository;

	@Autowired
	protected AlertRepository alertRepository;

	@Autowired
	private SimpMessagingTemplate template;

	private static final Logger logger = LoggerFactory
			.getLogger(StatsController.class);

	@RequestMapping(value = "/{id:(?!link|index|stats).*}+", method = RequestMethod.GET, produces = "text/html")
	public String redirectToStatistics(@PathVariable String id,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
			HttpServletRequest request, HttpServletResponse response,
			Model model) throws Exception {

		logger.info("Requested redirection to statistics with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		logger.info("From: " + from + ". To: " + to);

		if (l != null) {
			model.addAttribute("target", l.getTarget());
			model.addAttribute("date", l.getCreated());
			long click = clickRepository.clicksByHash(l.getHash(), from, to,
					null, null, null, null);
			model.addAttribute("clicks", click);
			model.addAttribute("from", from);
			model.addAttribute("to", to);

			/* Adds JSON array for clicks by city */
			DBObject groupObjectCity = clickRepository
					.getClicksByCity(id, from, to, null, null, null, null)
					.getRawResults();
			String listCities = groupObjectCity.get("retval").toString();
			String cityData = processCityJSON(listCities);
			model.addAttribute("clicksByCity", cityData);

			/* Adds JSON array for clicks by country */
			DBObject groupObject = clickRepository
					.getClicksByCountry(id, from, to).getRawResults();
			String list = groupObject.get("retval").toString();
			logger.info("JSON data 1: " + list);
			String countryData = processCountryJSON(list);
			logger.info("JSON data 2: " + countryData);
			model.addAttribute("clicksByCountry", countryData);
			System.out.println(countryData);
			response.setStatus(HttpStatus.OK.value());
			return "stats";
		}
		else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			throw new CustomException("404", "NOT_FOUND");
		}
	}

	@RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> statsJSON(@PathVariable String id,
			@RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
			@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
			@RequestParam(value = "min_lon", required = false) String minLon,
			@RequestParam(value = "max_lon", required = false) String maxLon,
			@RequestParam(value = "min_lat", required = false) String minLat,
			@RequestParam(value = "max_lat", required = false) String maxLat,
			HttpServletRequest request) {
		logger.info("Requested json with hash " + id);
		logger.info("From: " + from + ", To: " + to + ", minLat: " + minLat
				+ ", maxLat: " + maxLat + ", minLon: " + minLon + ", maxLon: "
				+ maxLon);

		/* Converts lat and lon strings to float if they are not null */
		Float latMin = null;
		Float latMax = null;
		Float lonMin = null;
		Float lonMax = null;
		if (minLon != null) {
			lonMin = Float.parseFloat(minLon);
		}
		if (maxLon != null) {
			lonMax = Float.parseFloat(maxLon);
		}
		if (minLat != null) {
			latMin = Float.parseFloat(minLat);
		}
		if (maxLat != null) {
			latMax = Float.parseFloat(maxLat);
		}
		DBObject groupObject = clickRepository.getClicksByCountry(id, from, to)
				.getRawResults();
		logger.info("obj: " + groupObject.toString());
		String list = groupObject.get("retval").toString();
		String countryList = StatsController.processCountryJSON(list);
		logger.info("list: " + countryList);
		DBObject groupObjectCity = clickRepository
				.getClicksByCity(id, from, to, latMin, lonMax, latMax, lonMin)
				.getRawResults();
		String listCities = groupObjectCity.get("retval").toString();
		String cityList = processCityJSON(listCities);
		logger.info("lonmin: " + lonMin + ", lonMax: " + lonMax + ", latMin: "
				+ latMin + ", latMax: " + latMax);
		ShortURL l = shortURLRepository.findByHash(id);
		StatsURL stats = new StatsURL(l.getTarget(), l.getCreated().toString(),
				clickRepository.clicksByHash(l.getHash(), from, to, latMin,
						lonMax, latMax, lonMin),
				from, to, latMin, latMax, lonMin, lonMax, countryList,
				cityList);
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}

	/**
	 * Converts a ResultsByGroup JSON text into a text array of elements in a
	 * format suitable for Google Charts API.
	 */
	public static String processCityJSON(String text) {
		String res = "[['latitude','longitude','clicks','city']";
		text = text.replace("[", "").replace("]", "").replace("{", "")
				.replace("}", "").replace(" ", "").replace("\"", "'");
		String[] parts = text.split(",");
		String aux = "";
		if (!text.equals("")) {
			for (int i = 0; i < parts.length; i++) {
				String[] keyValue = parts[i].split(":");
				if (keyValue[0].equals("'latitude'")) {
					res += ",[" + keyValue[1];
				}
				else if (keyValue[0].equals("'longitude'")) {
					res += "," + keyValue[1];
				}
				else if (keyValue[0].equals("'city'")) {
					aux = "," + keyValue[1] + "]";
				}
				else if (keyValue[0].equals("'count'")) {
					res += "," + keyValue[1] + aux;
				}
			}
			res += "]";
		}
		else {
			res = "";
		}

		return res;
	}

	/**
	 * Converts a ResultsByGroup JSON text into a text array of elements in a
	 * format suitable for Google Charts API.
	 */
	public static String processCountryJSON(String text) {
		String res = "[[\"Country\",\"Clicks\"]";
		text = text.replace("[", "").replace("]", "").replace("{", "")
				.replace("}", "").replace(" ", "");
		String[] parts = text.split(",");
		if (!text.equals("")) {
			for (int i = 0; i < parts.length; i++) {
				res += ",";
				String[] keyValue = parts[i].split(":");
				if (keyValue[0].equals("\"country\"")) {
					res += "[" + keyValue[1];
				}
				else if (keyValue[0].equals("\"count\"")) {
					res += keyValue[1] + "]";
				}
			}
		}
		res += "]";
		return res;
	}

	@RequestMapping(value = "/stats/filter/", method = RequestMethod.GET)
	public ResponseEntity<?> filterStats(
			@RequestParam(value = "id", required = true) String hash,
			@RequestParam(value = "from", required = true) String from,
			@RequestParam(value = "to", required = true) String to,
			@RequestParam(value = "min_latitude", required = true) Float min_latitude,
			@RequestParam(value = "max_longitude", required = true) Float max_longitude,
			@RequestParam(value = "max_latitude", required = true) Float max_latitude,
			@RequestParam(value = "min_longitude", required = true) Float min_longitude)
					throws ParseException {
		System.out.println(from + "entra");
		Date dateF = null;
		Date dateT = null;
		if (!from.equals("")) {
			dateF = new SimpleDateFormat("yyyy-MM-dd").parse(from);
		}
		if (!to.equals("")) {
			dateT = new SimpleDateFormat("yyyy-MM-dd").parse(to);
		}
		long clicks = clickRepository.clicksByHash(hash, dateF, dateT,
				min_latitude, max_longitude, max_latitude, min_longitude);
		/* Data from countries */
		DBObject groupObject = clickRepository
				.getClicksByCountry(hash, dateF, dateT).getRawResults();
		String list = groupObject.get("retval").toString();
		String countryData = StatsController.processCountryJSON(list);
		/* Data from cities */
		DBObject groupObjectCity = clickRepository
				.getClicksByCity(hash, dateF, dateT, min_latitude,
						max_longitude, max_latitude, min_longitude)
				.getRawResults();
		String listCities = groupObjectCity.get("retval").toString();
		String cityData = processCityJSON(listCities);
		System.out.println(clicks);
		WebSocketsData wb = new WebSocketsData(true, clicks, countryData,
				cityData);
		/* Send through the Web Ssocket */
		this.template.convertAndSend("/topic/" + hash, wb);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Given a request from a ShortURL stats page, returns true if that url is
	 * owned by authenticated user, false otherwise.
	 */
	@RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
	public ResponseEntity<String> checkIfOwner(
			@RequestParam(value = "url", required = true) String hash) {
		/* Retrieves authenticated user */
		String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		logger.info("Checking if user " + mail + " is owner of url");

		/* Retrieves owner's mail of link */
		hash = hash.substring(1, hash.length() - 1);
		ShortURL su = shortURLRepository.findByHash(hash);
		String owner = su.getOwner();

		/* Checks if authed user is owner of that link */
		if (owner != null && owner.equals(mail)) {
			logger.info("equals");
			Date expire = su.getExpire();
			String result = "";
			if (expire != null) {
				/* Only adds expire date if it has been introduced */
				result += su.getExpire().toString();
			}
			else {
				result += "never";
			}
			result += "##";
			Alert a = alertRepository.findByHash(su.getHash());
			if (a != null) {
				/* Only adds alert date if it has been introduced */
				result += a.getDate().toString();
			}
			else {
				result += "no alert specified";
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		else {
			logger.info("not equals");
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}

	/**
	 * Return the list of rules from a link
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRules", method = RequestMethod.GET, produces = "text/html")
	public ResponseEntity<?> getRules(
			@RequestParam(value = "url", required = true) String hash) {
		ShortURL l = shortURLRepository.findByHash(hash);
		if (l != null) {
			if (l.getRules() != null && !l.getRules().isEmpty()) {
				String rules = "";
				for (int i = 0; i < l.getRules().size(); i++) {
					rules += l.getRules().get(i).toString() + " ";
				}
				return new ResponseEntity<>(rules, HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Set or modify the rules about expiration of an URL
	 */
	@RequestMapping(value = "/setRules", method = RequestMethod.POST)
	public ResponseEntity<?> setRules(
			@RequestParam(value = "url", required = true) String hash,
			@RequestParam(value = "rule", required = true) String rule,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByHash(hash);
		if (l != null) {
			if (executeS(rule, l) != null) {
				l.addRule(rule);
				shortURLRepository.save(l);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>("No compila",
						HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/editRule", method = RequestMethod.POST)
	public ResponseEntity<?> editRules(
			@RequestParam(value = "url", required = true) String hash,
			@RequestParam(value = "rule", required = true) String rule,
			@RequestParam(value = "edit", required = true) String edit,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByHash(hash);
		if (l != null) {
			if (executeS(edit, l) != null) {

				ArrayList<String> array = l.getRules();
				array.set(Integer.valueOf(rule), edit);
				l.setRules(array);
				shortURLRepository.save(l);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/deleteRule", method = RequestMethod.POST)
	public ResponseEntity<?> deleteRules(
			@RequestParam(value = "url", required = true) String hash,
			@RequestParam(value = "rule", required = true) String rule,
			HttpServletRequest request) {
		ShortURL l = shortURLRepository.findByHash(hash);
		if (l != null) {

			l.removeRule(Integer.valueOf(rule));
			shortURLRepository.save(l);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Check if the rule can be executed, if not return null
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
