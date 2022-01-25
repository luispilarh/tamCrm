package com.tam.crm.controllers;

import com.tam.crm.model.Customer;
import com.tam.crm.services.CustomerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Customers")
@RestController
@RequestMapping(value = "v1/customers")
public class CustomerController {

	@Autowired
	private CustomerService service;

	@GetMapping
	public List<Customer> getCustomers() {
		return service.getCustomers();
	}

	@GetMapping("{id}")
	public Customer getCustomerById(@PathVariable Long id) {
		return service.getCustomer(id);
	}

	@PutMapping("{id}")
	public void updateCustomer(@RequestBody Customer customer) {
		service.updateCustomer(customer);
	}

	@DeleteMapping("{id}")
	public void deleteCustomer(@PathVariable Long id) {
		service.deleteCustomer(id);
	}

}
