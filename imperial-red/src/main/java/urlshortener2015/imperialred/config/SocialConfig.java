package urlshortener2015.imperialred.config;

import org.apache.tomcat.util.net.jsse.openssl.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.social.connect.ConnectionRepository;

//@Configuration
public class SocialConfig {

//	@Bean
//	    @Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
//	    public ConnectionRepository connectionRepository(){
//	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	        if (authentication == null) {
//	            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
//	        }
//	        return usersConnectionRepository().createConnectionRepository(authentication.getName());
//	    }

}
