package com.tam.crm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${security.enable}")
	boolean securityEnable;
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if(securityEnable) {
			http.authorizeRequests().anyRequest().authenticated().and().oauth2Login().and().csrf(csrf -> csrf.ignoringAntMatchers("/v1/**"));
		}else {
			http.cors().and().csrf().disable();
		}
	}

}
