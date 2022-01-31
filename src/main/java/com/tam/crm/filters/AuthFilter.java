package com.tam.crm.filters;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(1)
@ConditionalOnProperty(prefix = "security",name="enable",havingValue = "true")
public class AuthFilter implements Filter {

	@Autowired
	private AuthService authService;

	@Value("${auth.paths}")
	String authPaths;
	@Value("${admin.paths}")
	String adminPaths;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String path = req.getServletPath();
		List<String> loginPathsList = Arrays.asList(authPaths.split(","));
		List<String> securedPathsList = Arrays.asList(adminPaths.split(","));
		if (loginPathsList.stream().anyMatch(path::startsWith)) {
			try {
				User currentUser = authService.getCurrentUser();
				if (securedPathsList.stream().anyMatch(path::startsWith) && !currentUser.isAdmin()) {
					resp.sendError(HttpStatus.FORBIDDEN.value(), "The current user[" + currentUser.getUsername() + "] does not have permission for this operation");
					return;
				}
				chain.doFilter(request, response);
			} catch (UnregisteredUserException e) {
				resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Current user unauthorized");
				return;
			}
		} else {
			chain.doFilter(request, response);
		}

	}

}
