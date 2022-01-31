package com.tam.crm.services.impl;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class AuthServiceImplTest {

	@InjectMocks
	AuthServiceImpl authService;
	@Mock
	UserService userService;
	@Test
	void getCurrentUser() throws UnregisteredUserException {
		DefaultOAuth2User principal = Mockito.mock(DefaultOAuth2User.class);
		String username = "test";
		Mockito.when(principal.getAttribute("login")).thenReturn(username);
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(principal);
		User user = new User();
		user.setUsername(username);
		Mockito.when(userService.getUser(username)).thenReturn(user);
		Assertions.assertEquals(authService.getCurrentUser(),user);
	}
}