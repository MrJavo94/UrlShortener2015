package urlshortener2015.imperialred.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
			HttpServletRequest request) {

		logger.info("Requested json with hash " + id);
		logger.info("From: " + from);
		logger.info("To: " + to);
		ShortURL l = shortURLRepository.findByHash(id);
		StatsURL stats = new StatsURL(l.getTarget(), l.getCreated().toString(),
				clickRepository.clicksByHash(l.getHash(), from, to, null, null,
						null, null),
				from, to);
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
		String aux="";
		if (!text.equals("")) {
			for (int i = 0; i < parts.length; i++) {
				String[] keyValue = parts[i].split(":");
				if (keyValue[0].equals("'latitude'")) { 
					res += ",[" + keyValue[1];
				}
				else if (keyValue[0].equals("'longitude'")) {
					res += ","+keyValue[1] ;
				}
				else if (keyValue[0].equals("'city'")) {
					aux = ","+keyValue[1]+ "]";
				}
				else if (keyValue[0].equals("'count'")) {
					res += ","+keyValue[1]+aux ;
				}
			}
			res += "]";
		}
		else{
			res="";
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
		// countryData
		DBObject groupObject = clickRepository
				.getClicksByCountry(hash, dateF, dateT).getRawResults();
		String list = groupObject.get("retval").toString();
		String countryData = StatsController.processCountryJSON(list);
		// cityData
		DBObject groupObjectCity = clickRepository
				.getClicksByCity(hash, dateF, dateT, min_latitude,
						max_longitude, max_latitude, min_longitude)
				.getRawResults();
		String listCities = groupObjectCity.get("retval").toString();
		String cityData = processCityJSON(listCities);
		System.out.println(clicks);
		WebSocketsData wb = new WebSocketsData(true, clicks, countryData,
				cityData);
		this.template.convertAndSend("/topic/" + hash, wb);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Given a request from a ShortURL stats page, returns true if
	 * that url is owned by authenticated user, false otherwise.
	 */
	@RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
	public ResponseEntity<String> checkIfOwner(
			@RequestParam(value = "url", required = true) String hash) {
		/* Retrieves authenticated user */
		String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		logger.info("Checking if user " + mail + " is owner of url");
		
		/* Retrieves owner's mail of link */
		hash = hash.substring(1,hash.length()-1);
		ShortURL su = shortURLRepository.findByHash(hash);
		String owner = su.getOwner();
		
		/* Checks if authed user is owner of that link */
		if (owner!= null && owner.equals(mail)) {
			logger.info("equals");
			Date expire = su.getExpire();
			String result = "";
			if (expire != null) {
				/* Only adds expire date if it has been introduced */
				result += su.getExpire().toString();
			} else {
				result += "never";
			}
			result += "##";
			Alert a = alertRepository.findByHash(su.getHash());
			if (a != null) {
				/* Only adds alert date if it has been introduced */
				result += a.getDate().toString();
			} else {
				result += "no alert specified";
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		} else {
			logger.info("not equals");
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
	

}
