package com.tam.crm.services.impl;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.User;
import com.tam.crm.services.EmailService;
import com.tam.crm.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Async
public class EmailServiceImpl implements EmailService {

	@Value("${email.from}")
	String from;
	@Value("${crm.urlBase:http://localhost:8080}")
	String urlBase;
	@Value("${email.baseUrl}")
	protected String emailBaseUrl;
	@Value("${email.apiKey}")
	protected String apiKey;
	@Value("classpath:template.html")
	Resource template;

	@Autowired
	private UserService userService;
	@Autowired
	private RestTemplate restTemplate;

	private Log log = LogFactory.getLog(EmailServiceImpl.class);

	@Override
	public void sendCSVResult(List<ResultCSV> result, int toInsert, int inserted, User currentUser, String key) throws CrmDataException {
		List<String> toList = userService.getAdminEmails();
		try {
			if (!toList.isEmpty()) {
				String body = null;
				body = createBody(result, toInsert, inserted, currentUser, key);

				String subject = "Proccess CSV " + (result.isEmpty() && toInsert == inserted ? "SUCCESSFUL" : "FAILED");
				for (String to : toList) {
					//TODO implement queue politics
					HttpHeaders headers = new HttpHeaders();
					headers.setBasicAuth("api", apiKey);
					String urlTemplate = UriComponentsBuilder
						.fromHttpUrl(emailBaseUrl)
						.queryParam("from", from)
						.queryParam("to", to)
						.toUriString();
					MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
					map.add("html", body);
					map.add("subject", subject);
					HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
					postRequest(urlTemplate, request);
				}
			} else {
				log.error("Email not send, not found admin emails");
			}
		} catch (IOException e) {
			log.error("Email not send, not found template");
		}

	}

	private void postRequest(String urlTemplate, HttpEntity<MultiValueMap<String, String>> request) {
		try {
			log.info(restTemplate.postForEntity(urlTemplate, request, Map.class).getBody());
		} catch (HttpClientErrorException e) {
			log.error("email not send", e);
		}
	}

	@Override
	public String createBody(List<ResultCSV> result, int toInsert, int inserted, User currentUser, String key) throws IOException {
		boolean insertError = toInsert != inserted;
		Writer writer = new StringWriter();
		HashMap<String, Object> map = new HashMap<>();
		map.put("user", currentUser.getUsername());
		map.put("csv", urlBase + "/v1/util/bucktes/" + StorageServiceImpl.BUCKET_CSV+"/object?key="+key);
		map.put("status", (result.isEmpty() && !insertError) ? "SUCCESSFUL" : "FAILED");
		map.put("results", result);
		map.put("inserted", inserted);
		map.put("showTable", result.isEmpty()?"none":"");
		getTemplate().execute(writer, map);

		return writer.toString();
	}

	private Mustache getTemplate() throws IOException {
		MustacheFactory mf = new DefaultMustacheFactory();
		return mf.compile(new InputStreamReader(template.getInputStream()), "email");
	}

}
