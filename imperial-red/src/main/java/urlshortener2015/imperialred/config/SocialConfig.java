package urlshortener2015.imperialred.config;

import javax.inject.Inject;

import org.apache.tomcat.util.net.jsse.openssl.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import urlshortener2015.imperialred.repository.MongoConnectionTransformers;
import urlshortener2015.imperialred.repository.MongoUsersConnectionRepository;
import urlshortener2015.imperialred.social.GoogleAutoConfiguration;
import urlshortener2015.imperialred.web.CustomConnectController;

@Configuration
@EnableSocial
@EnableAutoConfiguration(exclude={GoogleAutoConfiguration.class, TwitterAutoConfiguration.class, FacebookAutoConfiguration.class})

public class SocialConfig implements SocialConfigurer{
	
	@Autowired
	MongoTemplate mongoTemplate;

//    @Inject
//    private Environment environment;
//	
//    @Bean
//    public ConnectionFactoryLocator connectionFactoryLocator() {
//    	
//        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
//        registry.addConnectionFactory(new GoogleConnectionFactory(
//            environment.getProperty("google.consumerKey"),
//            environment.getProperty("google.consumerSecret")));
//        return registry;
//    }
	
	
//    @Bean
//    public ConnectController connectController(
//                ConnectionFactoryLocator connectionFactoryLocator,
//                ConnectionRepository connectionRepository) {
//        return new ConnectController(connectionFactoryLocator, connectionRepository);
//    }
    

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
//		connectionFactoryConfigurer.addConnectionFactory(new TwitterConnectionFactory(
//				environment.getProperty("spring.social.twitter.appId"),
//				environment.getProperty("spring.social.twitter.appSecret")));
//		connectionFactoryConfigurer.addConnectionFactory(new FacebookConnectionFactory(
//				environment.getProperty("spring.social.facebook.appId"),
//				environment.getProperty("spring.social.facebook.appSecret")));
//		connectionFactoryConfigurer.addConnectionFactory(new GoogleConnectionFactory(
//				environment.getProperty("spring.social.google.appId"),
//				environment.getProperty("spring.social.google.appSecret")));
		
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new MongoUsersConnectionRepository(mongoTemplate, connectionFactoryLocator, new MongoConnectionTransformers(connectionFactoryLocator, Encryptors.noOpText()));
	}

}

