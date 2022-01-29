package com.tam.crm.services.impl;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.UserService;
import com.tam.crm.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	UserService userService;
	@Value("${security.enable}")
	boolean securityEnable;

	@Override
	public User getCurrentUser() throws UnregisteredUserException {
		if (!securityEnable)
			return userService.getUser("luispih");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
		String login = principal.getAttribute("login");
		return userService.getUser(login);
	}
}
