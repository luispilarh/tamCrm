package com.tam.crm.services.impl;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.User;
import com.tam.crm.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class EmailServiceImplTest {
	@InjectMocks
	EmailServiceImpl emailService;
	@Spy
	EmailServiceImpl spy;

	@Mock
	private UserService userService;
	@Mock
	private RestTemplate restTemplate;

	@Value("${email.from}")
	String from;
	@Value("${email.baseUrl}")
	protected String emailBaseUrl;
	@Value("${email.apiKey}")
	protected String apiKey;
	@Value("classpath:template.html")
	Resource template;
	@Value("classpath:resultTemplate.html")
	Resource resultTemplate;

	@Test
	void sendCSVResult() throws CrmDataException {
		List<String> mails = List.of("mail1", "mail2");
		Mockito.when(userService.getAdminEmails()).thenReturn(mails);
		emailService.template = template;
		emailService.apiKey = apiKey;
		emailService.from = from;
		emailService.emailBaseUrl = "http://mail.api";

		ArrayList<ResultCSV> result = new ArrayList<>();
		result.add(new ResultCSV(1l, ResultCSV.Level.WARN, "test"));
		result.add(new ResultCSV(2l, ResultCSV.Level.WARN, "test2"));
		String key = "key";
		User currentUser = new User();
		ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
		Mockito.when(responseEntity.getBody()).thenReturn("mail send");

		Mockito.when(restTemplate.postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.any())).thenReturn(responseEntity).thenThrow(
			HttpClientErrorException.class);
		emailService.sendCSVResult(result, 10, 10, currentUser, key);
		Mockito.verify(restTemplate, Mockito.times(2)).postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.any());
	}
	@Test
	void sendCSVResultFail() throws CrmDataException {
		List<String> mails = List.of();
		Mockito.when(userService.getAdminEmails()).thenReturn(mails);
		emailService.sendCSVResult(new ArrayList<>(), 10, 10, new User(), "key");
		Mockito.verify(restTemplate, Mockito.times(0)).postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.any());
	}
	@Test
	void sendCSVResultFailIOException() throws CrmDataException, IOException {
		List<String> mails = List.of("mail1", "mail2");
		spy.template = template;
		ReflectionTestUtils.setField(spy,"userService",userService);
		Mockito.when(userService.getAdminEmails()).thenReturn(mails);
		Mockito.when(spy.createBody(new ArrayList<>(),10,10,new User(),"key")).thenThrow(IOException.class);
		spy.sendCSVResult(new ArrayList<>(), 10, 10, new User(), "key");
		Mockito.verify(restTemplate, Mockito.times(0)).postForEntity(ArgumentMatchers.anyString(), ArgumentMatchers.any(HttpEntity.class), ArgumentMatchers.any());
	}

	@Test
	void createBody() throws IOException {
		emailService.template = template;
		ArrayList<ResultCSV> result = new ArrayList<>();
		result.add(new ResultCSV(1l, ResultCSV.Level.WARN, "test"));
		result.add(new ResultCSV(2l, ResultCSV.Level.WARN, "test2"));
		String html = emailService.createBody(result, 10, 10, new User(), "key");
		Assertions.assertEquals(html, StreamUtils.copyToString(resultTemplate.getInputStream(), StandardCharsets.UTF_8));
	}
}