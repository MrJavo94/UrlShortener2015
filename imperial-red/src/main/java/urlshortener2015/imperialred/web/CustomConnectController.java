package urlshortener2015.imperialred.web;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

import urlshortener2015.imperialred.config.SocialConfig;

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
	
	private ConnectionFactoryLocator connectionFactoryLocator;
	
	private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
	
	private ConnectSupport connectSupport;

	@Inject
	public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
		this.connectionRepository = connectionRepository;
		this.connectionFactoryLocator = connectionFactoryLocator;
		//super.setApplicationUrl("http://ired.ml");
	}
	
	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider.
     * @param providerId the provider ID to connect to
     * @param request the request
     * @return a RedirectView to the connection status page
	 */
	@Override
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(@PathVariable String providerId, NativeWebRequest request) {
		connectSupport = new ConnectSupport(sessionStrategy);
		try {
			OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
			Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);
			String uniqueId = "";
			switch(providerId){
			case("google"):
				google = (Google) connection.getApi();
				uniqueId = google.plusOperations().getGoogleProfile().getAccountEmail();
				break;
			case("facebook"):
				facebook = (Facebook) connection.getApi();
				uniqueId = facebook.userOperations().getUserProfile().getEmail();
				break;
			case("twitter"):
				twitter = (Twitter) connection.getApi();
				uniqueId = twitter.userOperations().getUserProfile().getScreenName();
				break;
			}
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(uniqueId, null, null));			
			addConnection(connection, connectionFactory, request);
		} catch (Exception e) {
			sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);
			logger.warn("Exception while handling OAuth2 callback (" + e.getMessage() + "). Redirecting to " + providerId +" connection status page.");
		}
		return connectionStatusRedirect(providerId, request);
	}
	
	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
     * @param providerId the provider ID to connect to
     * @param request the request
     * @return a RedirectView to the connection status page
	 */
	@Override
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
		connectSupport = new ConnectSupport(sessionStrategy);
		try {
			OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
			Connection<?> connection = connectSupport.completeConnection(connectionFactory, request);
			String uniqueId = "";
			switch(providerId){
			case("google"):
				google = (Google) connection.getApi();
				uniqueId = google.plusOperations().getGoogleProfile().getAccountEmail();
				break;
			case("facebook"):
				facebook = (Facebook) connection.getApi();
				uniqueId = facebook.userOperations().getUserProfile().getEmail();
				break;
			case("twitter"):
				twitter = (Twitter) connection.getApi();
				uniqueId = twitter.userOperations().getUserProfile().getScreenName();
				break;
			}
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(uniqueId, null, null));
			addConnection(connection, connectionFactory, request);
		} catch (Exception e) {
			sessionStrategy.setAttribute(request, PROVIDER_ERROR_ATTRIBUTE, e);
			logger.warn("Exception while handling OAuth1 callback (" + e.getMessage() + "). Redirecting to " + providerId +" connection status page.");
		}
		return connectionStatusRedirect(providerId, request);
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
			logger.info("Mail: " + google.plusOperations().getGoogleProfile().getAccountEmail());
		}
		
		//connectionRepository.removeConnections(providerId);
		return "redirect:/";
	}

	@RequestMapping(value = "/{providerId}/mail", method = RequestMethod.POST)
	public ResponseEntity<?> mailChecker(@PathVariable String providerId, NativeWebRequest request,
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
	
	private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) {
		try {
			connectionRepository.addConnection(connection);
			//postConnect(connectionFactory, connection, request);
		} catch (DuplicateConnectionException e) {
			sessionStrategy.setAttribute(request, DUPLICATE_CONNECTION_ATTRIBUTE, e);
		}
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
//		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
//			interceptor.postConnect(connection, request);
//		}
//	}

}