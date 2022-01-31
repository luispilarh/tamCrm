package com.tam.crm.services.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.daos.CustomerDao;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.CrmStorageException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.model.User;
import com.tam.crm.services.AuthService;
import com.tam.crm.services.CsvService;
import com.tam.crm.services.CustomerService;
import com.tam.crm.services.EmailService;
import com.tam.crm.services.StorageService;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDao dao;
	@Autowired
	private StorageService storageService;
	@Autowired
	private AuthService authService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CsvService csvService;


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
			if (dao.updateCustomer(id, customer, authService.getCurrentUser().getId()) != 1) {
				throw new CrmDataException("fail to update customer");
			}
		} catch (UnregisteredUserException | DataAccessException e) {
			throw new CrmDataException("Update customer failed. " + e.getMessage());
		}
	}

	@Override
	public void deleteCustomer(Long id) throws CrmDataException {
		try {
			if (dao.deleteCustomer(id, authService.getCurrentUser().getId()) != 1) {
				throw new CrmDataException("fail to delete customer");
			}
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
			photo = storageService.putObject(id, file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
		} catch (SdkClientException | IOException e) {
			throw new CrmStorageException(e.getMessage());
		}
		try {
			if (dao.updatePhoto(id, photo, authService.getCurrentUser().getId()) != 1) {
				throw new CrmDataException("fail to update customer");
			}
		} catch (UnregisteredUserException | DataAccessException e) {
			throw new CrmDataException(e.getMessage());
		}
	}

	@Override
	public List<ResultCSV> createCustomerBatch(MultipartFile file) throws CrmDataException {

		List<ResultCSV> result = new ArrayList<>();
		List<Customer> toInsert = new ArrayList<>();
		try {
			User currentUser = authService.getCurrentUser();
			String key = storageService.putObject(StorageServiceImpl.BUCKET_CSV, currentUser.getId(), file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
			csvService.process(result, toInsert, key);
			int[] ints = dao.insertBatch(toInsert, currentUser.getId());
			emailService.sendCSVResult(result, toInsert.size(), ints.length, currentUser, key);

		} catch (UnregisteredUserException e) {
			throw new CrmDataException("fail to create customers, user not found.");
		} catch (IOException e) {
			throw new CrmDataException("fail to create customers");
		}
		return result;
	}



}
