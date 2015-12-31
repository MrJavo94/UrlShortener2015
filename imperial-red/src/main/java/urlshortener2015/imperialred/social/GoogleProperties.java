package urlshortener2015.imperialred.social;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for Spring Social Twitter.
 *
 * @author Stephane Nicoll
 * @since 1.2.0
 */
@ConfigurationProperties("spring.social.google")
public class GoogleProperties extends SocialProperties {

}