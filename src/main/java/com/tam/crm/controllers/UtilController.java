package com.tam.crm.controllers;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.UserService;
import com.tam.crm.services.AuthService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "Util")
@RestController
@RequestMapping(value = "v1/util")
public class UtilController {

	@Autowired
	AuthService authService;
	@Autowired
	CacheManager cacheManager;

	@GetMapping("cache")
	public Map<String, Cache> getCache() {
		Map<String, Cache> ret = new HashMap<>();
		for (String name : cacheManager.getCacheNames()) {
			ret.put(name, cacheManager.getCache(name));
		}
		return ret;
	}

	@GetMapping("currentUser")
	public User currentUser() throws UnregisteredUserException {
		return authService.getCurrentUser();
	}
}
