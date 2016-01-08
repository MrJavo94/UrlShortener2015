package urlshortener2015.imperialred.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import urlshortener2015.imperialred.social.SimpleSocialUsersDetailService;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            		.antMatchers("/css/**/*", "/js/**/*", "/img/**/*", "/image/**/*", "/fonts/**/*", "/webjars/**/*", "/favicon.ico",
            				"/users", "/userlogin")
            		.permitAll()
                	.antMatchers("/**", "/**/*")
                	.authenticated()
            .and()
            	.formLogin()
            		.loginPage("/login")
            			.usernameParameter("email")
            			.passwordParameter("password")
            				.permitAll()
            .and()
                .logout()
                	.permitAll()
            .and()
            	.apply(new SpringSocialConfigurer())
            .and()
                .csrf()
                	.disable();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//            .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");
//    }
    
	@Bean
	public SocialUserDetailsService socialUsersDetailService() {
		return new SimpleSocialUsersDetailService(userDetailsService());
	}
}