package urlshortener2015.imperialred.web;

import java.net.URI;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
 
@Controller
@RequestMapping("/connect")
public class ConnectController extends org.springframework.social.connect.web.ConnectController {
	
	@Autowired
	private Twitter twitter;
 
    @Inject
    public ConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
        super(connectionFactoryLocator, connectionRepository);
    }
 
    @Override
    protected String connectedView(String providerId) {
    	TwitterProfile a = twitter.userOperations().getUserProfile();
    	System.out.println("Tengo " + a.getFollowersCount() + " seguidores.");
        return "redirect:/";
    }

    @RequestMapping(value="/{providerId}/check", method=RequestMethod.GET)
	private ResponseEntity<?> response() {
		HttpHeaders h = new HttpHeaders();
		twitter.userOperations().
		if(twitter.isAuthorized()){
			TwitterProfile a = twitter.userOperations().getUserProfile();		
			return new ResponseEntity<>(a,h,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(h,HttpStatus.FORBIDDEN);
		}
		
	}
 
}