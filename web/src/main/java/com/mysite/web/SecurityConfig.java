package com.mysite.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	 @Bean
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
	                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
	            .formLogin((formLogin) -> formLogin
	                    .loginPage("/user/login")
	                    .defaultSuccessUrl("/"))
	            .logout((logout) -> logout
	                    .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
	                    .logoutSuccessUrl("/")
	                    .invalidateHttpSession(true))
	        ;
	        return http.build();
	    }
	 
	 @Bean
	    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }

}
