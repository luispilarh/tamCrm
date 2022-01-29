package com.tam.crm.services;

import com.amazonaws.services.s3.model.S3Object;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.CrmStorageException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {
	List<Customer> getCustomers();

	Customer getCustomer(Long id);

	void updateCustomer(Long id, UpdateCustomer customer) throws UnregisteredUserException, CrmDataException;

	void deleteCustomer(Long id) throws UnregisteredUserException, CrmDataException;

	S3Object getPhotoCustomer(Long id);

	void updatePhotoCustomer(Long id, MultipartFile file) throws UnregisteredUserException, CrmStorageException, CrmDataException;
}
