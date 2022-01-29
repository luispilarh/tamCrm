package com.tam.crm.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.tam.crm.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {
	@Autowired
	private AmazonS3 s3;
	@Value("${minio.bucket}")
	private String bucket;

	@Override public List<S3ObjectSummary> listObjects(String name) {
		ObjectListing objectListing = s3.listObjects(name);
		return objectListing.getObjectSummaries();
	}

	@Override public List<Bucket> listBuckets() {
		return s3.listBuckets();
	}

	@Override
	public void getObject(String name, String object, ServletOutputStream outputStream) throws IOException {
		S3Object object1 = s3.getObject(name, object);
		IOUtils.copy(object1.getObjectContent(), outputStream);
	}

	@Override
	public S3Object getObject(String name) {
		return s3.getObject(bucket, name);
	}

	@Override
	public String putObject(Long id, String name, String contentType, Long contentLength, InputStream is) {

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		metadata.setContentLength(contentLength);
		String key = id + "/" + name;
		s3.putObject(bucket,key,is,metadata);
		return key;
	}
}
