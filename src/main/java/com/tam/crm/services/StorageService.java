package com.tam.crm.services;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface StorageService {
	List<S3ObjectSummary> listObjects(String name);

	List<Bucket> listBuckets();

	void getObject(String name, String object, ServletOutputStream outputStream) throws IOException;

	S3Object getObject(String name);

	boolean exitsObject(String name);

	String putObject(Long id, String name, String contentType, Long contentLength, InputStream is);
}
