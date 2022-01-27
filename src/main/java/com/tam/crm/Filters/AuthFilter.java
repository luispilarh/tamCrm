package com.tam.crm.Filters;

import com.tam.crm.model.User;
import com.tam.crm.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

//@Component
@Order(1)
public class AuthFilter implements Filter {
	@Autowired
	User currentUser;
	@Autowired
	AdminService adminService;

	@Value("${auth.paths}")
	String authPaths;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
//		String login = principal.getAttribute("login");
//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpServletResponse resp = (HttpServletResponse) response;
//		String path = req.getServletPath();
//		List<String> loginPathsList = Arrays.asList(authPaths.split(","));
//		if (loginPathsList.stream().anyMatch(path::startsWith)) {
//			try {
//				currentUser = adminService.getUser(login);
//				chain.doFilter(request, response);
//			} catch (UnregisteredUserException e) {
//				resp.sendError(HttpStatus.UNAUTHORIZED.value(), "Current user unauthorized");
//				return;
//			}
//		}else{
			chain.doFilter(request, response);
//		}

	}
}
