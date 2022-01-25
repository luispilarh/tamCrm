package com.tam.crm.services.impl;

import com.tam.crm.model.Customer;
import com.tam.crm.services.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Override public List<Customer> getCustomers() {
		return null;
	}

	@Override public Customer getCustomer(Long id) {
		return null;
	}

	@Override public void updateCustomer(Customer customer) {

	}

	@Override public void deleteCustomer(Long id) {

	}
}
