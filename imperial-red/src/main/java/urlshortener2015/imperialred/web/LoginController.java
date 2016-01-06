package urlshortener2015.imperialred.web;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import urlshortener2015.imperialred.objects.ErrorResponse;
import urlshortener2015.imperialred.objects.JsonResponse;
import urlshortener2015.imperialred.objects.SuccessResponse;
import urlshortener2015.imperialred.objects.User;
import urlshortener2015.imperialred.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Controller used for user login.
 * When a user wants to log-in, they pass an object with its username and password.
 * If correct, it returns a new Token, that has to be used on every request of the client
 * as an "Authorization bearer" header.
 */
@Controller
@RequestMapping("/login/login")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
    private String key = "jamarro";

    @Autowired
    protected UserRepository userRepository;


    public LoginController() {

    }

    public void setKey(String key){
        this.key = key;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String login(){
    	return "login";
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<? extends JsonResponse> login(HttpServletRequest request, HttpServletResponse response,
    		@RequestParam("username") String username, @RequestParam("password") String password)
            throws ServletException {
        if (username==null || username.isEmpty() || password==null ||
        		password.isEmpty()) {
            //No user o no password provided
            ErrorResponse errorResponse = new ErrorResponse("Please, provide both user and password");
            return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
        }
        else{
            User requestedUser = userRepository.findByMail(username);
            // Convert password to hash for comparing
            String hashedPw = Hash.makeHash(password);
            // Compares the requested user and both password hashes
            if(requestedUser!=null && requestedUser.getPassword().equals(hashedPw)){
                //User exists and password is correct

                //Expiration time of token
                Date expirationDate = new Date();
                long expirationTimeInSeconds = 360000;
                expirationDate.setTime(System.currentTimeMillis() + expirationTimeInSeconds *1000);

                //All right, generate Token
                Cookie cookie = new Cookie("NicolasMaduro",Jwts.builder().setSubject(username)
                        .setIssuedAt(new Date()).setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS512, key).compact());

                response.addCookie(cookie);
                return new ResponseEntity<>(
                        new SuccessResponse<>("OK"),
                        HttpStatus.OK);
            }
            else{
                //User or password incorrect
                ErrorResponse errorResponse = new ErrorResponse("User or password incorrect");
                return new ResponseEntity<>(errorResponse,HttpStatus.UNAUTHORIZED);
            }
        }


    }



}