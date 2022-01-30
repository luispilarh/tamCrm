package com.tam.crm.services.impl;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.User;
import com.tam.crm.services.EmailService;
import com.tam.crm.services.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Async
public class EmailServiceImpl implements EmailService {
	@Autowired
	WebClient emailWebClient;
	@Value("${email.from}")
	String from;
	@Value("${crm.urlBase:http://localhost:8080}")
	String urlBase;
	@Autowired
	UserService userService;

	private Log log = LogFactory.getLog(EmailServiceImpl.class);

	@Override
	public void sendCSVResult(List<ResultCSV> result, int toInsert, int inserted, User currentUser, String key) throws CrmDataException {
		List<String> toList = userService.getAdminEmails();
		if (toList.size() > 0) {
			String body = createBody(result, toInsert, inserted, currentUser, key);
			String subject = "Proccess CSV " + (result.size() == 0 && toInsert == inserted ? "SUCCESSFUL" : "FAILED");
			for (String to : toList) {
				log.info(emailWebClient
					.post()
					.uri(uriBuilder -> uriBuilder
						.queryParam("from", from)
						.queryParam("to", to)
						.queryParam("subject", subject).build()
					)
					.body(BodyInserters.fromFormData("html", body))
					.exchangeToMono(response -> {
						return response.bodyToMono(String.class);
					}));
			}
		} else {
			log.error("Email not send, not found admin emails");
		}
	}

	@Override
	public String createBody(List<ResultCSV> result, int toInsert, int inserted, User currentUser, String key) {
		String hostname = "";
		Boolean insertError = toInsert != inserted;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "unknow";
		}
		String html = "<body><h3> User " + currentUser.getUsername() + " upload <a href=\"" + urlBase + "/v1/util/bucktes/" + StorageServiceImpl.BUCKET_CSV + "/object?name=" + key
			+ "\">this csv</a> to create new customers. ";
		if (result.size() == 0 && !insertError) {
			html = html + "<span style=\"color:red;font-weight: bold;\">SUCCESSFUL</span> " + inserted + " customers created </h3></body>";
		} else {
			html = html + "<span style=\"color:red;font-weight: bold;\">FAILED</span> </h3>";
			if (insertError) {
				html = html + "<p style=\"color:red;font-weight: bold;\">The number of inserted elements is not correct</p>";
			}
			if (result.size() > 0) {
				html = html + "<table> <tr><th>Line</th><th>Level</th><th>Error message</th></tr>";
				html = html + result.stream().map(ResultCSV::toString).collect(Collectors.joining(""));
				html = html + "<p> <span style=\"color:red;font-weight: bold;\">ERROR</span> lines not inserted</p>";
				html = html + "<p> <span style=\"color:yellow;font-weight: bold;\">WARN</span> lines inserted</p>";
			}
			html = html + "<p> however 9 customers created</p>";
			html = html + "</body>";
		}
		return html;
	}
}
