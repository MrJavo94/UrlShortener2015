package urlshortener2015.imperialred.web;

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

import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.UserRepository;

@RestController
public class UsersController {
	
	@Autowired
	protected UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);
	
	/**
	 * Returns the user identified by {nick} in a JSON representation
	 */
	@Cacheable("users")
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.GET, produces = "application/json")
	public User getUser(@PathVariable String nick) {
		logger.info("Getting user from db");
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
		User user = new User(mail, nick, password, null, null);
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
