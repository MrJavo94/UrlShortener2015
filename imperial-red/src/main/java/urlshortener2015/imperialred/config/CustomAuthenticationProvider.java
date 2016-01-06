package urlshortener2015.imperialred.config;


import java.util.Date;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;

import com.google.common.hash.HashCode;
import com.mongodb.util.Hash;

import urlshortener2015.imperialred.repository.UserRepository;

public class CustomAuthenticationProvider implements AuthenticationProvider{
	
	private UserRepository userRepository;

	public CustomAuthenticationProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {



//        if (authentication.getPrincipal()==null || login.getUsername().isEmpty() || login.getPassword()==null ||
//                login.getPassword().isEmpty()) {
//            //No user o no password provided
//            ErrorResponse errorResponse = new ErrorResponse("Please, provide both user and password");
//            return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
//        }
//        else{

		
		return new UsernamePasswordAuthenticationToken("melon", "melon");
		
//            User requestedUser = userRepository.findByUsername(authentication.getPrincipal().toString());
//            // Convert password to hash for comparing
//            int password = Hash.hashBackward(authentication.getCredentials().toString());
//            // Compares the requested user and both password hashes
//            if(requestedUser!=null && requestedUser.getPassword().equals(password)){
//                
//            	return new UsernamePasswordAuthenticationToken(requestedUser.getUsername(), requestedUser.getPassword(),
//            			requestedUser.getAuthorities());
//
//            }
//            else{
//            	throw new BadCredentialsException("Password doesn't match");
//            }
//        }

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
