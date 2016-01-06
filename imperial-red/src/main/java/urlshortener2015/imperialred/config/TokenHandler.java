package urlshortener2015.imperialred.config;

import java.util.Date;

import org.springframework.security.core.userdetails.User;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import urlshortener2015.imperialred.repository.UserServiceRepository;

public final class TokenHandler {
	  
	      private final String secret;
	      private final UserServiceRepository userService;
	  
	      public TokenHandler(String secret, UserServiceRepository userService) {
	          this.secret = secret;
	          this.userService = userService;
	      }
	 
	     public User parseUserFromToken(String token) {
	         String username = Jwts.parser()
	                 .setSigningKey(secret)
	                 .parseClaimsJws(token)
	                 .getBody()
	                 .getSubject();
	         return userService.findUserByUsername(username);
	     }
	 
	     public String createTokenForUser(User user) {
	    	 Date expirationDate = new Date();
             long expirationTimeInSeconds = 3600;
             expirationDate.setTime(System.currentTimeMillis() + expirationTimeInSeconds *1000);
	    	 
	    	 
	         return Jwts.builder()
	                 .setSubject(user.getUsername())
	                 .setIssuedAt(new Date()).setExpiration(expirationDate)
	                 .signWith(SignatureAlgorithm.HS512, secret)
	                 .compact();
	     }
	 }