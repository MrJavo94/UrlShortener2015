package urlshortener2015.imperialred.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.GET)
	public User getUser(@PathVariable String nick) {
		return userRepository.findByNick(nick);
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public User addUser(@RequestParam(value = "mail", required = true) String mail,
			@RequestParam(value = "nick", required = true) String nick,
			@RequestParam(value = "password", required = true) String password) {
		User user = new User(mail, nick, password, null, null);
		return userRepository.save(user);
	}
	
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.PUT)
	public User modifyUser(@PathVariable String nick,
			@RequestParam(value = "password", required = true) String password) {
		User user = userRepository.findByNick(nick);
		User userModified = new User(user.getId(), user.getMail(), user.getNick(),
				user.getPassword(), user.getCreatedLinks(), user.getAvailableLinks());
		return userRepository.save(userModified);
	}
	
	@RequestMapping(value = "/users/{nick}", method = RequestMethod.DELETE)
	public String deleteUser(@PathVariable String nick) {
		userRepository.deleteByNick(nick);
		return "/users";
	}
	
}
