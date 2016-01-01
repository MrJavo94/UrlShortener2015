package urlshortener2015.imperialred.config;

import javax.inject.Inject;

import org.apache.tomcat.util.net.jsse.openssl.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.google.connect.GoogleConnectionFactory;

@Configuration
public class SocialConfig {

    @Inject
    private Environment environment;
	
    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
    	
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new GoogleConnectionFactory(
            environment.getProperty("google.consumerKey"),
            environment.getProperty("google.consumerSecret")));
        return registry;
    }

}

