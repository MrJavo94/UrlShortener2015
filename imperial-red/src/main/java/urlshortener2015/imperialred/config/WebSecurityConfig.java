package urlshortener2015.imperialred.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.social.security.SocialAuthenticationProvider;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import urlshortener2015.imperialred.repository.UserRepository;
import urlshortener2015.imperialred.repository.UserServiceRepository;
import urlshortener2015.imperialred.social.SimpleSocialUsersDetailService;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserServiceRepository userService;

	@Autowired
	private UserRepository userRepository;

	private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

	private TokenAuthenticationService tokenAuthenticationService;

	public WebSecurityConfig() {
		super(true);
		tokenAuthenticationService = new TokenAuthenticationService("jamarro", userService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// F filter = postProcess(authFilter);
		// http.addFilter(filter);
		
		ArrayList<AuthenticationProvider> authenticationProviders = new ArrayList();
		authenticationProviders.add(new CustomAuthenticationProvider(userRepository));
		
		ProviderManager providerManager = new ProviderManager(authenticationProviders);
		providerManager.setEraseCredentialsAfterAuthentication(false);

		http
			.authenticationProvider(new CustomAuthenticationProvider(userRepository)).exceptionHandling()
			.and()
				.anonymous()
			.and()
				.servletApi()
			.and()
				.authorizeRequests()
				.antMatchers("user/profile").permitAll()
				.anyRequest().permitAll()
			.and()
				.formLogin()
				.loginPage("/login/login").permitAll()
			.and()
				.logout().permitAll();
		
		CustomAuthenticationFilter authFilter = new CustomAuthenticationFilter(tokenAuthenticationService);
		
		authFilter.setAuthenticationManager(providerManager);
		authFilter.setAuthenticationSuccessHandler(successHandler);
		authFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login/login?error"));
		SessionAuthenticationStrategy sessionAuthenticationStrategy = http
				.getSharedObject(SessionAuthenticationStrategy.class);
		if (sessionAuthenticationStrategy != null) {
			authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
		}
		RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
		if (rememberMeServices != null) {
			authFilter.setRememberMeServices(rememberMeServices);
		}
		
		http
			.apply(new SpringSocialConfigurer())
			.and()
				.addFilterAfter(authFilter,
						AbstractPreAuthenticatedProcessingFilter.class)
				.addFilterAfter(new StatelessAuthenticationFilter(tokenAuthenticationService),
						CustomAuthenticationFilter.class)

				// .addFilterBefore(new
				// StatelessAuthenticationFilter(tokenAuthenticationService),
				// new CustomAuthenticationFilter(tokenAuthenticationService))
				.csrf().disable()
				.headers().cacheControl();
	}

	// @Autowired
	// public void configureGlobal(AuthenticationManagerBuilder auth) throws
	// Exception {
	// auth
	// .inMemoryAuthentication()
	// .withUser("user").password("password").roles("USER");
	// }

	@Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SimpleSocialUsersDetailService(userDetailsService());
	}
}
