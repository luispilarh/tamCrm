package com.tam.crm;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrmConfiguration {

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

}
