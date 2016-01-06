package urlshortener2015.imperialred.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import urlshortener2015.imperialred.repository.UserServiceRepository;

public class TokenAuthenticationService {
	 
	     private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
	 
	     private final TokenHandler tokenHandler;
	 
	     public TokenAuthenticationService(String secret, UserServiceRepository userService) {
	         tokenHandler = new TokenHandler(secret, userService);
	     }
	 
	     public void addAuthentication(HttpServletResponse response, UserAuthentication authentication) {
	         final User user = (User) authentication.getDetails();
	         response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
	     }
	
	     public Authentication getAuthentication(HttpServletRequest request) {
	         final String token = request.getHeader(AUTH_HEADER_NAME);
	         if (token != null) {
	             final User user = tokenHandler.parseUserFromToken(token);
	             if (user != null) {
	                 return new UserAuthentication(user);
	             }
	         }
	         return null;
	     }
	 }