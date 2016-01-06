package urlshortener2015.imperialred.config;

import java.util.ArrayList;
import java.util.Date;

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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import urlshortener2015.imperialred.repository.MongoConnectionTransformers;
import urlshortener2015.imperialred.repository.MongoUsersConnectionRepository;
import urlshortener2015.imperialred.social.GoogleAutoConfiguration;

@Configuration
@EnableSocial
@EnableAutoConfiguration(exclude = { GoogleAutoConfiguration.class, TwitterAutoConfiguration.class,
		FacebookAutoConfiguration.class })

public class SocialConfig implements SocialConfigurer {

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,
			Environment environment) {
		// connectionFactoryConfigurer.addConnectionFactory(new
		// TwitterConnectionFactory(
		// environment.getProperty("spring.social.twitter.appId"),
		// environment.getProperty("spring.social.twitter.appSecret")));
		// connectionFactoryConfigurer.addConnectionFactory(new
		// FacebookConnectionFactory(
		// environment.getProperty("spring.social.facebook.appId"),
		// environment.getProperty("spring.social.facebook.appSecret")));
		// connectionFactoryConfigurer.addConnectionFactory(new
		// GoogleConnectionFactory(
		// environment.getProperty("spring.social.google.appId"),
		// environment.getProperty("spring.social.google.appSecret")));

	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new MongoUsersConnectionRepository(mongoTemplate, connectionFactoryLocator,
				new MongoConnectionTransformers(connectionFactoryLocator, Encryptors.noOpText()));
	}

//	@Bean
//    @Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
//    public ConnectionRepository connectionRepository(UsersConnectionRepository usersConnectionRepository){
//    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//        	ArrayList<GrantedAuthority> listaPermisos = new ArrayList<GrantedAuthority>();
//        	listaPermisos.add(new SimpleGrantedAuthority("PUTA"));
//            return usersConnectionRepository.createConnectionRepository(            		
//            		new AnonymousAuthenticationToken("Anonimo KIO", new Date() , listaPermisos).getName()); 
//        }
//        return usersConnectionRepository.createConnectionRepository(authentication.getName());
//    }

}
