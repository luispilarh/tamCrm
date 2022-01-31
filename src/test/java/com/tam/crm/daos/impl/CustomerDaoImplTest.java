package com.tam.crm.daos.impl;

import com.tam.crm.TestConfiguration;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@JdbcTest
@Import(CustomerDaoImpl.class)
@ContextConfiguration(classes = TestConfiguration.class)
class CustomerDaoImplTest {

	@Autowired
	CustomerDaoImpl dao;
	@Test
	void selectCustomers() {
		Assertions.assertNotNull(dao.selectCustomers());
	}

	@Test
	void findCustomerById() {
		Assertions.assertEquals("pepe",dao.findCustomerById(1l).getName());
	}

	@Test
	void updateCustomer() {
		Customer customerById = dao.findCustomerById(1l);
		Assertions.assertEquals(1,dao.updateCustomer(1l,getUpdateCustomer(),1l));
	}

	@Test
	void deleteCustomer() {
		int size = dao.selectCustomers().size();
		dao.deleteCustomer(1l,1l);
		Assertions.assertNotEquals(size,dao.selectCustomers().size());
	}

	@Test
	void updatePhoto() {
		String photo = "uri/photo";
		dao.updatePhoto(1l, photo,1l);
		Assertions.assertEquals(dao.findCustomerById(1l).getPhoto(),photo);
	}

	@Test
	void insertBatch() {
		int size = dao.selectCustomers().size();
		List<Customer> list = List.of(getCustomer());
		dao.insertBatch(list,1l);
		Assertions.assertNotEquals(size,dao.selectCustomers().size());
		Assertions.assertEquals(size + list.size(),dao.selectCustomers().size());
	}

	@Test
	void existCustomer() {
		Assertions.assertTrue(dao.existCustomer("pepe","perez"));
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
		customer.setPhoto("photo");
		customer.setLasUpdatedBy("userName");
		customer.setSurname("surname");
		customer.setName("name");
		customer.setEmail("email");
		return customer;
	}

}