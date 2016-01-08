package urlshortener2015.imperialred;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import urlshortener2015.imperialred.objects.URLProtection;
import urlshortener2015.imperialred.objects.WebTokenFilter;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	private String key = "jamarro";
	
	public static void main(String[] args) throws Exception {
		//SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		WebTokenFilter authenticationFilter = new WebTokenFilter(key);

		//Protect all methods from "/link"
		URLProtection linkURL = new URLProtection("/link.*");
		linkURL.setAllMethods();
		authenticationFilter.addUrlToProtect(linkURL);
		
		//Protect all methods from "/link"
		URLProtection homeURL = new URLProtection("/");
		homeURL.setAllMethods();
		authenticationFilter.addUrlToProtect(homeURL);

		//Protect GET, DELETE and PUT from "/user"
		URLProtection userURL = new URLProtection("/user.*");
		userURL.addMethod("GET");
		userURL.addMethod("DELETE");
		userURL.addMethod("PUT");
		authenticationFilter.addUrlToProtect(userURL);

		//Protect GET from aggregated link information
		URLProtection aggregatedInfoURL = new URLProtection("/info.*");
		aggregatedInfoURL.addMethod("GET");
		authenticationFilter.addUrlToProtect(aggregatedInfoURL);


		//Protect GET from simple link information
		URLProtection infoURL = new URLProtection("/.*\\+");
		infoURL.addMethod("GET");
		authenticationFilter.addUrlToProtect(infoURL);

		registrationBean.setFilter(authenticationFilter);

		return registrationBean;
	}
}
