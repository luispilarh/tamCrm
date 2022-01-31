package com.tam.crm.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class StorageServiceImplTest {
	@InjectMocks
	StorageServiceImpl storageService;

	@Mock
	private AmazonS3 s3;
	@Value("${minio.bucket}")
	protected String bucket;

	@Test
	void listObjects() {
		ObjectListing objectListing = Mockito.mock(ObjectListing.class);
		List<S3ObjectSummary> list = new ArrayList<>();
		Mockito.when(objectListing.getObjectSummaries()).thenReturn(list);
		Mockito.when(s3.listObjects(bucket)).thenReturn(objectListing);
		Assertions.assertEquals(list, storageService.listObjects(bucket));
	}

	@Test
	void listBuckets() {
		List<Bucket> list = new ArrayList<>();
		Mockito.when(s3.listBuckets()).thenReturn(list);
		Assertions.assertEquals(list,storageService.listBuckets());
	}

	@Test
	void getImage() {
		storageService.bucket=bucket;
		String key = "key";
		S3Object s3Object = new S3Object();
		Mockito.when(s3.getObject(bucket, key)).thenReturn(s3Object);
		Assertions.assertEquals(s3Object,storageService.getImage(key));
	}

	@Test
	void exitsImage() {
		storageService.bucket=bucket;
		String key = "key";
		Mockito.when(s3.doesObjectExist(bucket, key)).thenReturn(true);
		Assertions.assertTrue(storageService.exitsImage(key));
	}

	@Test
	void putImage() throws UnsupportedEncodingException {
		storageService.bucket=bucket;
		Mockito.when(s3.doesBucketExistV2(bucket)).thenReturn(false);
		String name = "test";
		String keyResult = storageService.putImage(1l, name, MediaType.IMAGE_JPEG_VALUE, 10l, new StringInputStream("test"));
		Assertions.assertTrue(keyResult.endsWith(name));
	}
}