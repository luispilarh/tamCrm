package com.tam.crm.controllers;

import com.tam.crm.TestConfiguration;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import com.tam.crm.services.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = UtilController.class)
@ContextConfiguration(classes = TestConfiguration.class)
class UtilControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private AuthService authService;
	@MockBean
	private CacheManager cacheManager;
	@MockBean
	private StorageService storageService;

	@Test
	void getCache() throws Exception {
		String cache = "cache";
		Mockito.when(cacheManager.getCacheNames()).thenReturn(List.of());
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/util/cache");
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Assertions.assertNotNull(mvcResult.getResponse().getContentAsString());
	}

	@Test
	void currentUser() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/util/currentUser");
		Mockito.when(authService.getCurrentUser()).thenReturn(new User());
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void listBuckets() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/util/buckets");
		Mockito.when(storageService.listBuckets()).thenReturn(List.of());
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void listObject() throws Exception {
		String bucket = "test";
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/util/bucktes/{name}", bucket);
		Mockito.when(storageService.listObjects(bucket)).thenReturn(List.of());
		mockMvc.perform(request).andExpect(status().isOk());
	}

}