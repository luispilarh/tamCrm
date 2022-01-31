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
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {
	@Autowired
	private AmazonS3 s3;
	@Value("${minio.bucket}")
	private String bucket;
	public static final String BUCKET_CSV = "csv";

	@Override public List<S3ObjectSummary> listObjects(String bucket) {
		ObjectListing objectListing = s3.listObjects(bucket);
		return objectListing.getObjectSummaries();
	}

	@Override public List<Bucket> listBuckets() {
		return s3.listBuckets();
	}

	@Override
	public void getObject(String bucket, String key, ServletOutputStream outputStream) throws IOException {
		S3Object object1 = s3.getObject(bucket, key);
		IOUtils.copy(object1.getObjectContent(), outputStream);
	}

	@Override
	public S3Object getImage(String key) {
		return s3.getObject(bucket, key);
	}
	@Override
	public S3Object getObject(String bucket, String key) {
		return s3.getObject(bucket, key);
	}
	@Override
	public boolean exitsImage(String key) {
		return s3.doesObjectExist(bucket, key);
	}
	@Override
	public String putImage(Long id, String name, String contentType, Long contentLength, InputStream is) {

		return this.putObjet(bucket,id,name,contentType,contentLength,is);
	}

	@Override
	public String putObjet(String bucket, Long id, String name, String contentType, Long contentLength, InputStream is) {
		if(!s3.doesBucketExistV2(bucket)){
			s3.createBucket(bucket);
		}
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		metadata.setContentLength(contentLength);
		String key = id + "/"+ UUID.randomUUID() + name;
		s3.putObject(bucket,key,is,metadata);
		return key;
	}
}
