package urlshortener2015.imperialred.config;

import org.springframework.social.UserIdSource;

public class CustomUserIdSource implements UserIdSource {

	@Override
	public String getUserId() {
		// TODO Auto-generated method stub
		return "polla";
	}

}
