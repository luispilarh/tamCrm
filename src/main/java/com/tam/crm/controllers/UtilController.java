package com.tam.crm.controllers;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import com.tam.crm.services.EmailService;
import com.tam.crm.services.StorageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "Util")
@RestController
@RequestMapping(value = "v1/util")
public class UtilController {

	@Autowired
	private AuthService authService;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private EmailService emailService;
	@Autowired
	private StorageService storageService;

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

	@GetMapping("/buckets")
	public List<Bucket> listBuckets() {
		return storageService.listBuckets();
	}

	@GetMapping("/bucktes/{name}")
	public List<S3ObjectSummary> testMinio(@PathVariable String name) {
		return storageService.listObjects(name);
	}

	@GetMapping("/bucktes/{name}/object")
	public void getObject(@PathVariable("name") String name, @RequestParam(value = "name", required = true) String object, HttpServletResponse response) throws IOException {
		storageService.getObject(name, object, response.getOutputStream());
		response.addHeader("Content-disposition", "attachment;filename=" + object);
		response.setContentType(URLConnection.guessContentTypeFromName(object));
		response.flushBuffer();
	}
}
