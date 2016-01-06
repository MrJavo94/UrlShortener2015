package urlshortener2015.imperialred.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import urlshortener2015.imperialred.exception.CustomException;
import urlshortener2015.imperialred.objects.ShortURL;
import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.AlertRepository;
import urlshortener2015.imperialred.repository.ShortURLRepository;
import urlshortener2015.imperialred.repository.UserRepository;

@RestController
public class UsersController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	@Autowired
	protected AlertRepository alertRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
	
	/**
	 * Returns a view with the list of links of a user
	 */
	@RequestMapping(value = "/userlinks", method = RequestMethod.GET, produces = "text/html")
	public ResponseEntity<String> getUserLinks(HttpServletRequest request) {
		//String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		// TODO: get real email when that part is fixed. Until then, always test@expire
		String mail = "test@expire";
		logger.info("Getting links made by " + mail);
		User user = userRepository.findByMail(mail);
		logger.info("User: " + user);
		
		/* If user exists, retrieves its shortURLs */
		if (user != null) {
			List<ShortURL> links = shortURLRepository.findByOwner(mail);
			String urls = "";
			for (int i=0; i<links.size(); i++) {
				logger.info("ShortURL: " + links.get(i));
				logger.info(">" + links.get(i).getTarget());
				urls += links.get(i).getTarget() + " ";
			}
			return new ResponseEntity<>(urls, HttpStatus.CREATED);
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
