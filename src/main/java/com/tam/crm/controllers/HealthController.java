package com.tam.crm.controllers;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Health")
@RestController
@RequestMapping(value = "v1/health")
public class HealthController {

	@GetMapping
	public String health() {
		return "ok";
	}
}
