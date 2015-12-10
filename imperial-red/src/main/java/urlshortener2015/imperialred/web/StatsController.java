package urlshortener2015.imperialred.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	@RequestMapping(value = "/{id:(?!link|index|stats).*}+", method = RequestMethod.GET, produces = "text/html")
	public String redirectToStatistics(@PathVariable String id,
			HttpServletRequest request, Model model) throws Exception {

		logger.info("Requested redirection to statistics with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		if (l != null) {
			model.addAttribute("target", l.getTarget());
			model.addAttribute("date", l.getCreated());
			model.addAttribute("clicks",
					clickRepository.clicksByHash(l.getHash()));
			return "stats";
		}
		else {
			throw new CustomException("404", "NOT_FOUND");
		}
	}

	@RequestMapping(value = "/{id:(?!link|index).*}+", method = RequestMethod.GET, produces = "aplication/json")
	public ResponseEntity<?> statsJSON(@PathVariable String id,
			HttpServletRequest request) {

		logger.info("Requested json with hash " + id);
		ShortURL l = shortURLRepository.findByHash(id);
		StatsURL stats = new StatsURL(l.getTarget(), l.getCreated().toString(),
				clickRepository.clicksByHash(l.getHash()));
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}

}
