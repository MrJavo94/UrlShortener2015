package urlshortener2015.imperialred.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DBObject;

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.StatsURL;
import urlshortener2015.imperialred.objects.WebSocketsData;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;

@Controller
public class StatsController {

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	ShortURLRepository shortURLRepository;

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
			long click = clickRepository.clicksByHash(l.getHash(), from, to);
			model.addAttribute("clicks", click);
			model.addAttribute("from", from);
			model.addAttribute("to", to);

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
				clickRepository.clicksByHash(l.getHash(), from, to), from, to);
		return new ResponseEntity<>(stats, HttpStatus.OK);
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
		res += "]";
		return res;
	}

	@RequestMapping(value = "/stats/filter/", method = RequestMethod.GET)
	public ResponseEntity<?> filterStats(
			@RequestParam(value = "id", required = true) String hash,
			@RequestParam(value = "from", required = true) String from,
			@RequestParam(value = "to", required = true) String to)
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
		long clicks = clickRepository.clicksByHash(hash, dateF, dateT);
		DBObject groupObject = clickRepository
				.getClicksByCountry(hash, dateF, dateT).getRawResults();
		String list = groupObject.get("retval").toString();
		String countryData = StatsController.processCountryJSON(list);
		System.out.println(clicks);
		WebSocketsData wb = new WebSocketsData(true, clicks, countryData);
		this.template.convertAndSend("/topic/" + hash, wb);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Given a request from a ShortURL stats page, returns true if
	 * that url is owned by authenticated user, false otherwise.
	 */
	@RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
	public ResponseEntity<Boolean> checkIfOwner(
			@RequestParam(value = "url", required = true) String hash) {
		//String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		// TODO: get real email when that part is fixed. Until then, always test@expire
		String mail = "test@expire";
		
		/* Retrieves owners' mail of link */
		ShortURL su = shortURLRepository.findByHash(hash);
		String owner = su.getOwner();
		
		/* Checks if authed user is owner of that link */
		if (owner.equals(mail)) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}
	

}
