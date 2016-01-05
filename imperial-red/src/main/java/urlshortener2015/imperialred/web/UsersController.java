package urlshortener2015.imperialred.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.ShortURLRepository;
import urlshortener2015.imperialred.repository.UserRepository;

@RestController
public class UsersController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
	
	/**
	 * Returns a view with the list of links of a user
	 */
	@RequestMapping(value = "/userlinks", method = RequestMethod.GET, produces = "text/html")
	public ResponseEntity<List<ShortURL>> getUserLinks(HttpServletRequest request) {
		String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		logger.info("Getting links made by " + mail);
		User user = userRepository.findByMail(mail);
		
		/* If user exists, retrieves its shortURLs */
		if (user != null) {
			List<ShortURL> links = shortURLRepository.findByOwner(mail);
			return new ResponseEntity<>(links, HttpStatus.CREATED);
		} else {
			/* Throws 404 if user does not exist */
			throw new CustomException("404", "NOT_FOUND");
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
