package com.tam.crm.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.tam.crm.daos.CustomerDao;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.CrmStorageException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.services.AuthService;
import com.tam.crm.services.CustomerService;
import com.tam.crm.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	CustomerDao dao;
	@Autowired
	StorageService storageService;
	@Autowired
	AuthService authService;

	@Override
	public List<Customer> getCustomers() {
		return dao.selectCustomers();
	}

	@Override
	public Customer getCustomer(Long id) {
		return dao.findCustomerById(id);
	}

	@Override
	public void updateCustomer(Long id, UpdateCustomer customer) throws CrmDataException {
		try {
			dao.updateCustomer(id, customer, authService.getCurrentUser().getId());
		} catch (UnregisteredUserException | DataAccessException e) {
			throw new CrmDataException("Update customer failed. " + e.getMessage());
		}
	}

	@Override
	public void deleteCustomer(Long id) throws CrmDataException {
		try {
			dao.deleteCustomer(id, authService.getCurrentUser().getId());
		} catch (UnregisteredUserException | DataAccessException e) {
			throw new CrmDataException("Delete customer failed. " + e.getMessage());
		}
	}

	@Override
	public S3Object getPhotoCustomer(Long id) {
		Customer customerById = dao.findCustomerById(id);
		return storageService.getObject(customerById.getPhoto());
	}

	@Override
	public void updatePhotoCustomer(Long id, MultipartFile file) throws CrmStorageException, CrmDataException {
		String photo;
		try {
			photo = storageService.putObject(id, file.getName(), file.getContentType(), file.getSize(), file.getInputStream());
		} catch (SdkClientException | IOException e) {
			throw new CrmStorageException(e.getMessage());
		}
		try {
			dao.updatePhoto(id, photo, authService.getCurrentUser().getId());
		} catch (UnregisteredUserException | DataAccessException e) {
			throw new CrmDataException(e.getMessage());
		}
	}

}
