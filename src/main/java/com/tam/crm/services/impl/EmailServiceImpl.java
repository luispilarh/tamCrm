package com.tam.crm.services.impl;

import com.tam.crm.model.CrmEmail;
import com.tam.crm.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
	WebClient emailWebClient;
	@Value("${email.from}")
	String from;

	@Override
	public void send(String to, CrmEmail crmEmail) {
		System.out.println(emailWebClient
			.post()
			.uri(uriBuilder -> uriBuilder
				.queryParam("from", from)
				.queryParam("to", to)
				.queryParam("subject", crmEmail.getSubject())
				.queryParam("text", crmEmail.getText()).build()
			).retrieve().bodyToMono(String.class).block());
	}
}
