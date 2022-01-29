package com.tam.crm.daos;

import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;

import java.util.List;

public interface CustomerDao {
	List<Customer> selectCustomers();

	Customer findCustomerById(Long id);

	void updateCustomer(Long id, UpdateCustomer customer, Long userId);

	void deleteCustomer(Long id, Long userId);

	void updatePhoto(Long id, String photo, Long userId);

	String selectPhotoCustomer(Long id);
}
