package com.tam.crm.controllers;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.tam.crm.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("v1/storage")
public class StorageControlller {

	@Autowired
	private StorageService service;

	@GetMapping("/buckets")
	public List<Bucket> listBuckets() {
		return service.listBuckets();
	}

	@GetMapping("/bucktes/{name}")
	public List<S3ObjectSummary> testMinio(@PathVariable String name) {
		return service.listObjects(name);
	}

	@GetMapping("/bucktes/{name}/{object}")
	public void getObject(@PathVariable("name") String name, @PathVariable("object") String object, HttpServletResponse response) throws IOException {
		service.getObject(name, object, response.getOutputStream());
		response.addHeader("Content-disposition", "attachment;filename=" + object);
		response.setContentType(URLConnection.guessContentTypeFromName(object));
		response.flushBuffer();
	}
}
