package urlshortener2015.imperialred.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
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
	 * Returns the user identified by {nick} in a JSON representation
	 */
	@Cacheable("users")
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.GET, produces = "application/json")
	public User getUser(@PathVariable String nick) {
		logger.info("Getting user " + nick + " from db");
		return userRepository.findByNick(nick);
	}
	
	/**
	 * Creates a new user with the specified mail, nick and password.
	 * XXX: It's using POST, should it use PUT?
	 * XXX: JSON empty lists are being passed with null, is that correct?
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = "application/json")
	public User addUser(@RequestParam(value = "mail", required = true) String mail,
			@RequestParam(value = "nick", required = true) String nick,
			@RequestParam(value = "password", required = true) String password) {
		System.out.println(mail);
		System.out.println(nick);
		System.out.println(password);
		User user = new User(mail, nick, Hash.makeHash(password), null);
		return userRepository.save(user);
	}
	
	/**
	 * Modifies the password of the user identified by {nick}
	 * XXX: NOT WORKING (error 405: JSPs only permit GET POST or HEAD)
	 */
	@CachePut("users")
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.PUT, produces = "application/json")
	public User modifyUser(@PathVariable String nick,
			@RequestParam(value = "password", required = true) String password) {
		User user = userRepository.findByNick(nick);
		user.setPassword(password);
		return userRepository.save(user);
	}
	
	/**
	 * Deletes the user identified by {nick}
	 * XXX: I don't know what should be returned here
	 */
	@CacheEvict("users")
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.DELETE, produces = "application/json")
	public List<User> deleteUser(@PathVariable String nick) {
		userRepository.deleteByNick(nick);
		return userRepository.findAll();
	}
	
}
