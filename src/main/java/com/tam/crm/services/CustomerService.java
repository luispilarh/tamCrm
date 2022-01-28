package com.tam.crm.services;

import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
	List<Customer> getCustomers();

	Customer getCustomer(Long id);

	void updateCustomer(Long id, UpdateCustomer customer);

	void deleteCustomer(Long id);

	byte[] getPhotoCustomer(Long id);

	void updatePhotoCustomer(Long id, MultipartFile file);
}
