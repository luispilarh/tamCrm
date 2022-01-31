package com.tam.crm.controllers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.TestConfiguration;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.CrmStorageException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = CustomerController.class)
@ContextConfiguration(classes = TestConfiguration.class)
class CustomerControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private CustomerService customerService;
	@Autowired
	ObjectMapper objectMapper;

	@Test
	void getCustomers() throws Exception {
		Customer customer = getCustomer();
		List<Customer> list = List.of(customer);
		Mockito.when(customerService.getCustomers()).thenReturn(list);
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/customers");
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Mockito.verify(customerService, Mockito.times(1)).getCustomers();
		Assertions.assertEquals(list,
			objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Customer>>() {
			}));
	}

	@Test
	void getCustomerById() throws Exception {
		Customer customer = getCustomer();
		Mockito.when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/customers/" + customer.getId());
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Mockito.verify(customerService, Mockito.times(1)).getCustomer(customer.getId());
	}

	@Test
	void updateCustomer() throws Exception {
		UpdateCustomer customer = getUpdateCustomer();
		Mockito.doNothing().when(customerService).updateCustomer(1l, customer);
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/customers/1")
			.content(objectMapper.writeValueAsBytes(customer))
			.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk());
		Mockito.verify(customerService, Mockito.times(1)).updateCustomer(1l, customer);
	}

	@Test
	void updateCustomer_throwsExeception() throws Exception {
		UpdateCustomer customer = getUpdateCustomer();
		Mockito.doThrow(CrmDataException.class).when(customerService).updateCustomer(1l, customer);
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/customers/1")
			.content(objectMapper.writeValueAsBytes(customer))
			.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().is(500));
	}

	@Test
	void deleteCustomer() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.delete("/v1/customers/1");
		mockMvc.perform(request).andExpect(status().isOk());
		Mockito.verify(customerService, Mockito.times(1)).deleteCustomer(1l);
	}

	@Test
	void getPhotoCustomerById() throws Exception {
		S3Object photo = new S3Object();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(10l);
		metadata.setContentType(MediaType.IMAGE_JPEG_VALUE);
		photo.setObjectMetadata(metadata);
		String test = "test";
		photo.setObjectContent(new StringInputStream(test));
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/customers/1/photo");
		Mockito.when(customerService.getPhotoCustomer(1l)).thenReturn(photo);
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Assertions.assertEquals(mvcResult.getResponse().getContentType(), MediaType.IMAGE_JPEG_VALUE);
		Assertions.assertEquals(mvcResult.getResponse().getContentAsString(), test);
	}

	@Test
	void updatePhotoCustomer() throws Exception, CrmStorageException {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, new StringInputStream("test"));
		RequestBuilder request = MockMvcRequestBuilders.multipart("/v1/customers/1/photo")
			.file(mockMultipartFile)
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
		mockMvc.perform(request).andExpect(status().isOk());
		Mockito.verify(customerService, Mockito.times(1)).updatePhotoCustomer(1l, mockMultipartFile);
	}

	@Test
	void createBatch() throws Exception {
		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test", MediaType.MULTIPART_FORM_DATA_VALUE, new StringInputStream("test"));
		Mockito.when(customerService.createCustomerBatch(mockMultipartFile)).thenReturn(List.of());
		RequestBuilder request = MockMvcRequestBuilders.multipart("/v1/customers")
			.file(mockMultipartFile)
			.contentType(MediaType.MULTIPART_FORM_DATA_VALUE);
		mockMvc.perform(request).andExpect(status().isOk());
		Mockito.verify(customerService, Mockito.times(1)).createCustomerBatch(mockMultipartFile);

	}

	private UpdateCustomer getUpdateCustomer() {
		UpdateCustomer customer = new UpdateCustomer();
		customer.setSurname("surname");
		customer.setName("name");
		customer.setEmail("email");
		return customer;
	}

	private Customer getCustomer() {
		Customer customer = new Customer();
		customer.setId(1l);
		customer.setPhoto("photo");
		customer.setLasUpdatedBy("userName");
		customer.setSurname("surname");
		customer.setName("name");
		customer.setEmail("email");
		return customer;
	}
}