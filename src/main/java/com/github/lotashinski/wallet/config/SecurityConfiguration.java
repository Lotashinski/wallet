package com.github.lotashinski.wallet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(request -> 
							request
								.requestMatchers(HttpMethod.GET, "/error", "/favicon.ico", "/style/**", "/js/**")
									.permitAll()
								.requestMatchers("/registration").permitAll()
								.anyRequest().authenticated()
						)
				.formLogin(lf -> 
							lf
								.loginPage("/login")
							    .permitAll()
						)
				.logout(l -> 
							l
							.deleteCookies("JSESSIONID")
							.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						)
				.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}

	@Bean
    AuthenticationProvider authenticationProvider(UserDetailsService detailsService) {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(detailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
	
}
