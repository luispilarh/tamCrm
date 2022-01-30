package com.tam.crm;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerErrorException;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class CrmApplication {
	@Value("${minio.url}")
	private String minioURl;
	@Value("${minio.access-key}")
	private String accessKey;
	@Value("${minio.secret-key}")
	private String secretKey;
	@Value("${minio.bucket}")
	private String bucket;
	@Value("${minio.region}")
	private String region;
	@Value("${email.baseUrl}")
	private String emailBaseUrl;
	@Value("${email.apiKey}")
	private String apiKey;

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@Bean
	public AmazonS3 amazonS3() {
		AmazonS3 amazonS3 = AmazonS3Client.builder()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(minioURl, region))
			.withPathStyleAccessEnabled(true)
			.withClientConfiguration(new ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
			.build();
		if (!amazonS3.doesBucketExistV2(bucket)) {
			amazonS3.createBucket(bucket);
		}
		return amazonS3;
	}

	@Bean
	public WebClient emailWebClient() {
		return WebClient.builder()
			.baseUrl(emailBaseUrl)
			.defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("api", apiKey))
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.filter(ExchangeFilterFunction
			.ofResponseProcessor(CrmApplication::exchangeFilterResponseProcessor))
			.build();
	}
	private static Mono<ClientResponse> exchangeFilterResponseProcessor(ClientResponse response) {
		HttpStatus status = response.statusCode();
		if (!HttpStatus.OK.equals(status)) {
			return response.bodyToMono(String.class)
				.flatMap(body -> Mono.error(new ServerErrorException(body)));
		}
		return Mono.just(response);
	}

}
