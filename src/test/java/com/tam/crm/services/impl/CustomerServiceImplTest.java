package com.tam.crm.services.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import com.tam.crm.daos.CustomerDao;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.CrmStorageException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import com.tam.crm.services.CsvService;
import com.tam.crm.services.EmailService;
import com.tam.crm.services.StorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class CustomerServiceImplTest {
	private static final long userId = 1l;
	@InjectMocks
	CustomerServiceImpl customerService;
	@Mock
	private CustomerDao dao;
	@Mock
	private StorageService storageService;
	@Mock
	private AuthService authService;
	@Mock
	private EmailService emailService;
	@Mock
	private CsvService csvService;

	@BeforeEach
	void setup() throws UnregisteredUserException {
		User user = new User();
		user.setId(userId);
		Mockito.lenient().when(authService.getCurrentUser()).thenReturn(user);
	}

	@Test
	void getCustomers() {
		List<Customer> list = List.of(createCustomer());
		Mockito.when(dao.selectCustomers()).thenReturn(list);
		Assertions.assertEquals(customerService.getCustomers(), list);
	}

	@Test
	void getCustomer() {
		Customer customer = createCustomer();
		Mockito.when(dao.findCustomerById(1l)).thenReturn(customer);
		Assertions.assertEquals(customerService.getCustomer(1l), customer);
	}

	@Test
	void updateCustomer() throws CrmDataException {
		UpdateCustomer updateCustomer = createUpdateCustomer();
		Mockito.when(dao.updateCustomer(1l, updateCustomer, userId)).thenReturn(1);
		customerService.updateCustomer(1l, updateCustomer);
		Mockito.verify(dao, Mockito.times(1)).updateCustomer(1l, updateCustomer, userId);
	}
	@Test
	void updateCustomerFail()  {
		UpdateCustomer updateCustomer = createUpdateCustomer();
		Mockito.when(dao.updateCustomer(1l, updateCustomer, userId)).thenReturn(0);
		Assertions.assertThrows(CrmDataException.class,() -> customerService.updateCustomer(1l, updateCustomer));
	}
	@Test
	void updateCustomerFailUnregisteredUser() throws UnregisteredUserException {
		UpdateCustomer updateCustomer = createUpdateCustomer();
		Mockito.when(authService.getCurrentUser()).thenThrow(UnregisteredUserException.class);
		Assertions.assertThrows(CrmDataException.class,() -> customerService.updateCustomer(1l, updateCustomer));
	}



	@Test
	void deleteCustomer() throws CrmDataException {

		Mockito.when(dao.deleteCustomer(1l, userId)).thenReturn(1);
		customerService.deleteCustomer(1l);
		Mockito.verify(dao, Mockito.times(1)).deleteCustomer(1l, userId);
	}
	@Test
	void deleteCustomerFail()  {

		Mockito.when(dao.deleteCustomer(1l, userId)).thenReturn(0);
		Assertions.assertThrows(CrmDataException.class,() ->customerService.deleteCustomer(1l));

	}

	@Test
	void deleteCustomerFailUnregisteredUserException() throws UnregisteredUserException {

		Mockito.when(authService.getCurrentUser()).thenThrow(UnregisteredUserException.class);
		Assertions.assertThrows(CrmDataException.class,() ->customerService.deleteCustomer(1l));

	}

	@Test
	void getPhotoCustomer() {
		Customer customer = createCustomer();
		Mockito.when(dao.findCustomerById(1l)).thenReturn(customer);
		S3Object s3Object = new S3Object();
		Mockito.when(storageService.getImage(customer.getPhoto())).thenReturn(s3Object);
		Assertions.assertEquals(customerService.getPhotoCustomer(1l),s3Object);
	}

	@Test
	void updatePhotoCustomer() throws IOException, CrmDataException, CrmStorageException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(storageService.putImage(ArgumentMatchers.anyLong(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.any())).thenReturn(key);
		Mockito.when(dao.updatePhoto(1l,key,userId)).thenReturn(1);
		customerService.updatePhotoCustomer(1l,file);
		Mockito.verify(dao,Mockito.times(1)).updatePhoto(1l,key,userId);
	}

	@Test
	void updatePhotoCustomerFailSdkClientException() throws IOException, CrmDataException, CrmStorageException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(storageService.putImage(ArgumentMatchers.anyLong(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.any())).thenThrow(
			SdkClientException.class);
		Assertions.assertThrows(CrmStorageException.class,() ->customerService.updatePhotoCustomer(1l,file));
	}
	@Test
	void updatePhotoCustomerFail() throws IOException, CrmDataException, CrmStorageException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(storageService.putImage(ArgumentMatchers.anyLong(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.any())).thenReturn(key);
		Mockito.when(dao.updatePhoto(1l,key,userId)).thenReturn(0);
		Assertions.assertThrows(CrmDataException.class,() ->customerService.updatePhotoCustomer(1l,file));
	}
	@Test
	void updatePhotoCustomerFailUnregisteredUserException() throws IOException, CrmDataException, CrmStorageException, UnregisteredUserException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(storageService.putImage(ArgumentMatchers.anyLong(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.any())).thenReturn(key);
		Mockito.when(authService.getCurrentUser()).thenThrow(UnregisteredUserException.class);
		Assertions.assertThrows(CrmDataException.class,() ->customerService.updatePhotoCustomer(1l,file));
	}


	@Test
	void createCustomerBatch() throws IOException, CrmDataException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(storageService.putObjet(ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.anyString(),ArgumentMatchers.anyString(),ArgumentMatchers.anyLong(),ArgumentMatchers.any())).thenReturn(key);
		Mockito.when(dao.insertBatch(ArgumentMatchers.anyList(),ArgumentMatchers.anyLong())).thenReturn(new int[0]);
		Assertions.assertNotNull(customerService.createCustomerBatch(file));

	}
	@Test
	void createCustomerBatchFail() throws IOException, UnregisteredUserException {
		StringInputStream contentStream = new StringInputStream("test");
		MockMultipartFile file = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, contentStream);
		String key = "key";
		Mockito.when(authService.getCurrentUser()).thenThrow(UnregisteredUserException.class);
		Assertions.assertThrows(CrmDataException.class,()->customerService.createCustomerBatch(file));
	}


	private Customer createCustomer() {
		Customer customer = new Customer();
		customer.setId(1l);
		customer.setPhoto("photo");
		customer.setLasUpdatedBy("userName");
		customer.setSurname("surname");
		customer.setName("name");
		customer.setEmail("email");
		return customer;
	}

	private UpdateCustomer createUpdateCustomer() {
		UpdateCustomer customer = new UpdateCustomer();
		customer.setEmail("newEmail");
		customer.setSurname("newSurname");
		customer.setName("name");
		return customer;
	}
}