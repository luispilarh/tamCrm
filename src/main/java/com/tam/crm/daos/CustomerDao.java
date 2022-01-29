package com.tam.crm.daos;

import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;

import java.util.List;

public interface CustomerDao {
	List<Customer> selectCustomers();

	Customer findCustomerById(Long id);

	int updateCustomer(Long id, UpdateCustomer customer, Long userId);

	int deleteCustomer(Long id, Long userId);

	int updatePhoto(Long id, String photo, Long userId);

	int[] insertBatch(List<Customer> toInsert, Long userId);

	boolean existCustomer(String name, String surname);
}
