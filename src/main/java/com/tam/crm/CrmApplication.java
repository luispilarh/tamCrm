package com.tam.crm;

import com.tam.crm.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
public class CrmApplication {

	private static final String[] AUTH_WHITELIST = {
		// -- Swagger UI v2
		"/v2/api-docs",
		"/swagger-resources",
		"/swagger-resources/**",
		"/configuration/ui",
		"/configuration/security",
		"/swagger-ui.html",
		"/webjars/**",
		// -- Swagger UI v3 (OpenAPI)
		"/v3/api-docs/**",
		"/swagger-ui/**"
		// other public endpoints of your API may be appended to this array
	};

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@Bean
	public User getCurrentUser() {
		return new User();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		//		http.httpBasic().disable();
		http
			.authorizeRequests()
			.antMatchers(AUTH_WHITELIST).permitAll()
			.antMatchers("/v1**").authenticated()
			.and()
			.oauth2Login();
		return http.build();
	}
	//	@Bean
	//	public WebMvcConfigurer webMvcConfigurer() {
	//		return new WebMvcConfigurer() {
	//		};
	//	}

}
