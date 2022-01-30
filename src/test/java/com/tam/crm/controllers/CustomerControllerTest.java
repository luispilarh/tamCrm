package com.tam.crm.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.TestConfiguration;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
		Mockito.when(customerService.getCustomers()).thenReturn(List.of(customer));
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/customers");
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Mockito.verify(customerService, Mockito.times(1)).getCustomers();
	}



	@Test
	void getCustomerById() throws Exception {
		Customer customer = getCustomer();
		Mockito.when(customerService.getCustomer(customer.getId())).thenReturn(customer);
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/customers/"+customer.getId());
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Mockito.verify(customerService, Mockito.times(1)).getCustomer(customer.getId());
	}

	@Test
	void updateCustomer() throws Exception {
		UpdateCustomer customer = getCustomer();
		Mockito.doNothing().when(customerService).updateCustomer(1l,customer);
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/customers/1")
			.content(objectMapper.writeValueAsBytes(customer))
			.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk());
	}
	@Test
	void updateCustomer_throwsExeception() throws Exception {
		UpdateCustomer customer = getUpdateCustomer();
		Mockito.doThrow(CrmDataException.class).when(customerService).updateCustomer(1l,customer);
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/customers/1")
			.content(objectMapper.writeValueAsBytes(customer))
			.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().is(200));
	}


	@Test
	void deleteCustomer() {
		UpdateCustomer customer = getCustomer();
		//Mockito.doNothing().when(customerService).updateCustomer(1l,customer);
	}

	@Test
	void getPhotoCustomerById() {
	}

	@Test
	void updatePhotoCustomer() {
	}

	@Test
	void createBatch() {
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