package urlshortener2015.imperialred.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import urlshortener2015.imperialred.objects.Hash;
import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.UserRepository;

/**
 * Controller used for user login. When a user wants to log-in, they pass an
 * object with its username and password. If correct, it returns a new Token,
 * that has to be used on every request of the client as an
 * "Authorization bearer" header.
 */
@Controller
public class LoginController {

	@Autowired
	protected UserRepository userRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	public LoginController() {}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/**
	 * Creates a new user from the form
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = "text/html")
	public String addUser(@RequestParam(value = "mail", required = true) String mail,
			@RequestParam(value = "password", required = true) String password) {
		User prev = userRepository.findByMail(mail);
		/*
		 * Mail already used
		 */
		if (prev != null) {
			return null;
		}
		User user = new User(mail, Hash.makeHash(password));
		
		userRepository.save(user);
		
		/* POSIBLE ALMACENAMIENTO DE AUTORIZACION EN BD */
		ArrayList<GrantedAuthority> ja = new ArrayList();
		ja.add(new SimpleGrantedAuthority("USER"));
		
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mail, password, ja));
		
		return "redirect:/";
	}
	
	/**
	 * Creates a new user from the form
	 */
	@RequestMapping(value = "/users/disconnect", method = RequestMethod.DELETE)
	public ResponseEntity<?> disconnectUser(NativeWebRequest request) {
		SecurityContextHolder.getContext().setAuthentication(null);
		HttpHeaders h = new HttpHeaders();
		return new ResponseEntity<>(h, HttpStatus.ACCEPTED);
	}
	
	/**
	 * Checks id the mail provided is already registered
	 */
	@RequestMapping(value = "/users/checkmail", method = RequestMethod.GET)
	public ResponseEntity<?> checkMailUsed(@RequestParam(value = "mail", required = true) String email, NativeWebRequest request) {
		User requestedUser = userRepository.findByMail(email);
		HttpHeaders h = new HttpHeaders();
		if(requestedUser == null){
			return new ResponseEntity<>(h, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(h, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/userlogin", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "email", required = true) String email,
			@RequestParam(value = "password", required = true) String password, Model model) throws ServletException {

		User requestedUser = userRepository.findByMail(email);
		// Convert password to hash for comparing
		String hashedPw = Hash.makeHash(password);
		// Compares the requested user and both password hashes
		if (requestedUser != null && requestedUser.getPassword().equals(hashedPw)) {

			/* POSIBLE ALMACENAMIENTO DE AUTORIZACION EN BD */
			ArrayList<GrantedAuthority> ja = new ArrayList();
			ja.add(new SimpleGrantedAuthority("USER"));
			/*
			 * Adds to the context the authentication
			 */
			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(email, password, ja));
			
			String red = (String) request.getSession().getAttribute("redirect");
			request.getSession().removeAttribute("redirect");
			if(red != null){
				return "redirect:/" + red;
			}
			return "redirect:/";
		} else {
			// User or password incorrect
			model.addAttribute("msgerror", "User os password incorrect");
			return "login";
		}

	}
	
	/**
	 * Deletes a user from the system and redirects him to login page
	 */
	@RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
	public String deleteUser(HttpServletRequest request) {
		/* Retrieves authenticated user */
		String mail = UrlShortenerControllerWithLogs.getOwnerMail();
		logger.info("Deleting user " + mail);
		
		/* Deletes user from database */
		userRepository.deleteByMail(mail);
		
		/* Removes current session from user */
		SecurityContextHolder.getContext().setAuthentication(null);
		
		return "redirect:login";
	}

}