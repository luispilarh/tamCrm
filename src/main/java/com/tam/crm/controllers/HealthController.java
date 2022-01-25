package com.tam.crm.controllers;

import io.swagger.annotations.Api;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Health")
@RestController
@RequestMapping(value = "v1/health")
public class HealthController {

	@GetMapping
	public String health() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

		return principal.getAttribute("login");
	}
}
