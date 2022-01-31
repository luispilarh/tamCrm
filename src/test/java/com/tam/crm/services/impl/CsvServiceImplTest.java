package com.tam.crm.services.impl;

import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.daos.CustomerDao;
import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.services.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class CsvServiceImplTest {

	@InjectMocks
	CsvServiceImpl csvService;

	@Mock
	CustomerDao customerDao;
	@Mock
	StorageService storageService;
	@Value("classpath:example.csv")
	Resource csv;
	@Test
	void process() throws IOException {
		ArrayList<ResultCSV> result = new ArrayList<>();
		ArrayList<Customer> toInsert = new ArrayList<>();
		HashMap<Integer, Long> inserted = new HashMap<>();
		String key = "key";
		S3Object s3Object = new S3Object();
		s3Object.setObjectContent(csv.getInputStream());
		csvService.fields=new String[]{"photo", "name", "surname", "email"};
		csvService.quote='"';
		csvService.separator=',';
		csvService.objectMapper= new ObjectMapper();
		Mockito.when(storageService.exitsImage("photo1")).thenReturn(true);
		Mockito.when(storageService.exitsImage("photo2")).thenReturn(false);
		Mockito.when(customerDao.existCustomer("customer8","surname8")).thenReturn(true);
		Mockito.when(customerDao.existCustomer("customer1","surname1")).thenReturn(false);
		Mockito.when(customerDao.existCustomer("customer2","surname2")).thenReturn(false);
		Mockito.when(storageService.getObject(StorageServiceImpl.BUCKET_CSV,key)).thenReturn(s3Object);
		csvService.process(result, toInsert, key);
		Assertions.assertEquals(result.size(),6);
		Assertions.assertEquals(toInsert.size(),2);
	}


}