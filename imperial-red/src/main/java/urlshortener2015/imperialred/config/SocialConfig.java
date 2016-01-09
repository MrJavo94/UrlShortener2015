package urlshortener2015.imperialred.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration;
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import urlshortener2015.imperialred.repository.MongoConnectionTransformers;
import urlshortener2015.imperialred.repository.MongoUsersConnectionRepository;
import urlshortener2015.imperialred.social.GoogleAutoConfiguration;

@Configuration
@EnableSocial
@EnableAutoConfiguration(exclude={GoogleAutoConfiguration.class, TwitterAutoConfiguration.class, FacebookAutoConfiguration.class})

public class SocialConfig implements SocialConfigurer{
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new MongoUsersConnectionRepository(mongoTemplate, connectionFactoryLocator, new MongoConnectionTransformers(connectionFactoryLocator, Encryptors.noOpText()));
	}

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer,Environment environment) {}

}
