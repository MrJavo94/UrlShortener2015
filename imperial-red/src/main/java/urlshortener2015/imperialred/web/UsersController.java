package urlshortener2015.imperialred.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.UserRepository;

@RestController
public class UsersController {
	
	@Autowired
	protected UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
	
	/**
	 * Handles POST requests from clients authenticated with Google.
	 * TODO: Do something with the recovered email (database, etc)
	 * TODO: Handle exceptions properly
	 * TODO: Change hard-coded values
	 * TODO: Return response to XHR (js function in index.html)
	 * XXX: CURRENTLY NOT USED
	 */
	@RequestMapping(value = "/google-login", method = RequestMethod.POST)
	public void googleLogin(@RequestParam(value = "idtoken", required = true) String idTokenString) {
		/* Created token verifier */
		HttpTransport transport = new ApacheHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
				.setAudience(Arrays.asList("152937226054-ajhhaao7c41md0p7mrti9pgrcaag6nf7.apps.googleusercontent.com")).build();
		logger.info("Verifier created");

		/* Verifies if the sent token is ok */
		GoogleIdToken idToken = null;
		try {
			idToken = verifier.verify(idTokenString);
			logger.info("Verified");
		} catch (IOException e) {
			logger.info("Error retrieving ID token.");
		} catch (GeneralSecurityException e) {
			logger.info("General security exception.");
		}
		
		if (idToken != null) {
			String email = idToken.getPayload().getEmail();
			//Do insertions, etc
		} else {
			logger.info("Invalid ID token.");
		}
		
	}
	
	/**
	 * Returns the user identified by {mail} in a JSON representation
	 */
	@Cacheable("users")
	@RequestMapping(value = "/users/{mail}", method = RequestMethod.GET, produces = "application/json")
	public User getUser(@PathVariable String mail) {
		logger.info("Getting user " + mail + " from db");
		return userRepository.findByMail(mail);
	}
	
	/**
	 * Returns a view with the list of links of a user
	 */
	@RequestMapping(value = "/users/{mail}", method = RequestMethod.GET, produces = "text/html")
	public String getUserLinks(@PathVariable String mail, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		logger.info("Loading links page for user " + mail);
		
		return "links";
	}
	
	/**
	 * Creates a new user from a JSON object.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = "application/json")
	public User addUser(@RequestBody User user) {
		logger.info("New user: " + user.getMail() + " " + user.getNick() + " " + 
				user.getPassword() + " " + user.getTwitter());
		return userRepository.save(user);
	}
	
	/**
	 * Modifies the password of the user identified by {mail}
	 */
	@CachePut("users")
	@RequestMapping(value = "/users/{mail}", method = RequestMethod.POST, produces = "application/json")
	public User modifyUser(@PathVariable String mail,
			@RequestBody String password) {
		User user = userRepository.findByMail(mail);
		user.setPassword(password);
		return userRepository.save(user);
	}
	
	/**
	 * Deletes the user identified by {mail}
	 * XXX: I don't know what should be returned here
	 */
	@CacheEvict("users")
	@RequestMapping(value = "/users/{mail}", method = RequestMethod.DELETE, produces = "application/json")
	public List<User> deleteUser(@PathVariable String mail) {
		userRepository.deleteByMail(mail);
		return userRepository.findAll();
	}
	
}
