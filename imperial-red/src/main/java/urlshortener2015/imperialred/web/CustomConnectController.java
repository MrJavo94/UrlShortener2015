package urlshortener2015.imperialred.web;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.google.api.Google;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

@Controller
@Scope("session")
@RequestMapping("/connect")
public class CustomConnectController extends ConnectController {

	private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

	@Autowired
	private Twitter twitter;

	@Autowired
	private Facebook facebook;
	
	@Autowired
	private Google google;

	private String mail;

	private ConnectionRepository connectionRepository;

	@Inject
	public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
		this.connectionRepository = connectionRepository;
		//super.setApplicationUrl("http://ired.ml");
	}

	protected String connectedView(String providerId) {
		if (providerId.equals("facebook")) {
			facebook = (Facebook) connectionRepository.getPrimaryConnection(Facebook.class).getApi();
			String mail = facebook.userOperations().getUserProfile().getEmail();
			boolean sd = facebook.isAuthorized();
			logger.info("AUTH: " + sd);
			logger.info("mail: " + mail);
		} else if (providerId.equals("twitter")) {
			twitter = (Twitter) connectionRepository.getPrimaryConnection(Twitter.class).getApi();
			String twitterName = twitter.userOperations().getUserProfile().getScreenName();
			/* GUARDADO EN LA BASE DE DATOS DEL PAR TWITTER-MAIL */
			// TODO
			/* HACER */
		} else if (providerId.equals("google")){
			google = (Google) connectionRepository.getPrimaryConnection(Google.class).getApi();
			String mail = google.plusOperations().getGoogleProfile().getAccountEmail();
			logger.info("Mail: " + mail);
		}
		
		connectionRepository.removeConnections(providerId);
		return "redirect:/";
	}

	@RequestMapping(value = "/{providerId}/mail", method = RequestMethod.POST)
	public ResponseEntity<?> connect(@PathVariable String providerId, NativeWebRequest request,
			@RequestParam(value = "mail", required = true) String mail) {
		this.mail = mail;
		HttpHeaders h = new HttpHeaders();
		return new ResponseEntity<>(h, HttpStatus.OK);
	}

	@Deprecated
	@RequestMapping(value = "/{providerId}/check", method = RequestMethod.GET)
	private ResponseEntity<?> response() {
		HttpHeaders h = new HttpHeaders();
		if (connectionRepository.findConnections(Twitter.class) != null) {
			twitter = (Twitter) connectionRepository.getPrimaryConnection(Twitter.class).getApi();
			twitter.userOperations().getAccountSettings();
			TwitterProfile a = twitter.userOperations().getUserProfile();
			return new ResponseEntity<>(a, h, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(h, HttpStatus.FORBIDDEN);
		}
	}

}