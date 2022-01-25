package com.tam.crm.services;

import com.tam.crm.model.Customer;

import java.util.List;

public interface CustomerService {
	List<Customer> getCustomers();

	Customer getCustomer(Long id);

	void updateCustomer(Customer customer);

	void deleteCustomer(Long id);
}
