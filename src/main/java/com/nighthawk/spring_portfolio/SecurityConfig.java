package com.nighthawk.spring_portfolio;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import com.nighthawk.spring_portfolio.mvc.jwt.JwtAuthenticationEntryPoint;
import com.nighthawk.spring_portfolio.mvc.jwt.JwtRequestFilter;
import com.nighthawk.spring_portfolio.mvc.person.PersonDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/*
* To enable HTTP Security in Spring, extend the WebSecurityConfigurerAdapter. 
*/
@Configuration
@EnableWebSecurity // Beans to enable basic Web security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private PersonDetailsService personDetailsService;

	@Bean // Sets up password encoding style
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://127.0.0.1:4100")); // Add other allowed origins if
																						// needed
		configuration.setAllowedMethods(Arrays.asList( "DELETE", "GET", "POST", "PUT"));
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "x-csrf-token"));
		configuration.setAllowCredentials(true); // Allow credentials
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(personDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// Provide security configuration
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// no CSRF
				.csrf().disable()
				// list the requests/endpoints need to be authenticated
				.authorizeRequests()
				// Change "permitAll" to "authenticated" to enable authentication
				.antMatchers("/mvc/person/update/**").permitAll()
				.antMatchers("/api/person/**").permitAll()
				.antMatchers("/api/network/**").permitAll()
				.antMatchers("/**").permitAll()
				.and()
				// support cors
				.cors().and()
    			.headers()
    			.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Credentials", "true"))
    			.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-ExposedHeaders", "*", "Authorization"))
    			.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Content-Type",
    			        "Authorization", "x-csrf-token"))
    			.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-MaxAge", "600"))
    			.addHeaderWriter(
        			    new StaticHeadersWriter("Access-Control-Allow-Methods", "POST", "GET", "OPTIONS", "DELETE", "HEAD"))
			    // Remove the line below
			    	.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin",
			    	"http://127.0.0.1:4100"))
				.and()
				.formLogin()
				.loginPage("/login")
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/")
				.and()
				// make sure we use stateless session;
				// session won't be used to store user's state.
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}
}