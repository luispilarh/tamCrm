package com.tam.crm.Filters;

import com.tam.crm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Order(2)
public class AdminFilter implements Filter {
	@Autowired
	User currentUser;
	@Value("${admin.paths}")
	String adminPaths;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String path = req.getServletPath();
		List<String> securedPathsList = Arrays.asList(adminPaths.split(","));
		if (securedPathsList.stream().anyMatch(path::startsWith) && !currentUser.isAdmin()) {
			resp.sendError(HttpStatus.FORBIDDEN.value(), "The current user["+currentUser.getUserName()+"] does not have permission for this operation");
			return;
		}
		chain.doFilter(req, response);

	}
}
