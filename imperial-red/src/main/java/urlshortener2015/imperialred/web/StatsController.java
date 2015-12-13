package urlshortener2015.imperialred.web;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.DBObject;

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.StatsURL;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.repository.ClickRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;

@Controller
public class StatsController {

	@Autowired
	protected ClickRepository clickRepository;

	@Autowired
	ShortURLRepository shortURLRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(StatsController.class);

	@RequestMapping(value = "/{id:(?!link|index|stats).*}+", 
			method = RequestMethod.GET, produces = "text/html")
	public String redirectToStatistics(@PathVariable String id,
			@RequestParam(value = "from", required = false) 
				@DateTimeFormat(pattern="ddMMyyyy") Date from,
			@RequestParam(value = "to", required = false) 
				@DateTimeFormat(pattern="ddMMyyyy") Date to,
			HttpServletRequest request, Model model) throws Exception {

		logger.info("Requested redirection to statistics with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		logger.info("From: "+ from + ". To: " + to);

		if (l != null) {
			model.addAttribute("target", l.getTarget());
			model.addAttribute("date", l.getCreated());
			model.addAttribute("clicks", clickRepository.clicksByHash(l.getHash()));
			
			/* Adds JSON array for clicks by country */
			DBObject groupObject = clickRepository.getClicksByCountry(id, from, to).getRawResults();
			String list = groupObject.get("retval").toString();
			logger.info("JSON data 1: " + list);
			String countryData = processCountryJSON(list);
			logger.info("JSON data 2: " + countryData);
			model.addAttribute("clicksByCountry", countryData);
			return "stats";
		} else {
			throw new CustomException("404", "NOT_FOUND");
		}
	}

	@RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> statsJSON(@PathVariable String id,
			HttpServletRequest request) {

		logger.info("Requested json with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		StatsURL stats = new StatsURL(l.getTarget(), l.getCreated().toString(),
				clickRepository.clicksByHash(l.getHash()));
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}
	
	/**
	 * Converts a ResultsByGroup JSON text into a text array of elements
	 * in a format suitable for Google Charts API.
	 */
	private String processCountryJSON(String text) {
		String res = "[[\"Country\",\"Clicks\"]";
		text = text.replace("[", "").replace("]", "").replace("{","")
				.replace("}", "").replace(" ", "");
		String[] parts = text.split(",");
		for (int i=0; i<parts.length; i++) {
			res += ",";
			String[] keyValue = parts[i].split(":");
			if (keyValue[0].equals("\"country\"")) {
				res += "[" + keyValue[1];
			} else if (keyValue[0].equals("\"count\"")) {
				res += keyValue[1] + "]";
			}
		}
		res += "]";
		return res;
	}

}

