package com.tam.crm.filters;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class AuthFilterTest {

	private static final String V_1_USER = "/v1/user";
	private static final String V_1 = "/v1";

	@Test
	void doFilter_noSecuredPath() throws ServletException, IOException {
		AuthFilter filter = new AuthFilter();
		filter.authService = Mockito.mock(AuthService.class);
		filter.authPaths = V_1;
		filter.adminPaths = V_1_USER;
		FilterChain chain = Mockito.mock(FilterChain.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		filter.doFilter(request, response, chain);
		Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
	}

	@Test
	void doFilter_SecuredPath_throwsUnregisterdUser() throws ServletException, IOException, UnregisteredUserException {
		AuthFilter filter = new AuthFilter();
		AuthService authService = Mockito.mock(AuthService.class);
		filter.authService = authService;
		filter.authPaths = V_1;
		filter.adminPaths = V_1_USER;
		FilterChain chain = Mockito.mock(FilterChain.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/v1/customers");
		Mockito.when(authService.getCurrentUser()).thenThrow(UnregisteredUserException.class);
		filter.doFilter(request, response, chain);
		Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
		Mockito.verify(chain, Mockito.times(0)).doFilter(request, response);
	}

	@Test
	void doFilter_AdminPath_Forbidden() throws ServletException, IOException, UnregisteredUserException {
		AuthFilter filter = new AuthFilter();
		AuthService authService = Mockito.mock(AuthService.class);
		filter.authService = authService;
		filter.authPaths = V_1;
		filter.adminPaths = V_1_USER;
		FilterChain chain = Mockito.mock(FilterChain.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/v1/user");
		User value = new User();
		value.setAdmin(false);
		Mockito.when(authService.getCurrentUser()).thenReturn(value);
		filter.doFilter(request, response, chain);
		Assertions.assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
		Mockito.verify(chain, Mockito.times(0)).doFilter(request, response);
	}

	@Test
	void doFilter_AdminPath() throws ServletException, IOException, UnregisteredUserException {
		AuthFilter filter = new AuthFilter();
		AuthService authService = Mockito.mock(AuthService.class);
		filter.authService = authService;
		filter.authPaths = V_1;
		filter.adminPaths = V_1_USER;
		FilterChain chain = Mockito.mock(FilterChain.class);
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setServletPath("/v1/user");
		User value = new User();
		value.setAdmin(true);
		Mockito.when(authService.getCurrentUser()).thenReturn(value);
		filter.doFilter(request, response, chain);
		Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
	}
}