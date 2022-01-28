package com.tam.crm.daos;

import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;

import java.util.List;

public interface CustomerDao {
	List<Customer> selectCustomers();

	Customer findCustomerById(Long id);

	void updateCustomer(Long id, UpdateCustomer customer);

	void deleteCustomer(Long id);

	void updatePhoto(Long id, byte[] bytes);

	byte[] selectPhotoCustomer(Long id);
}
