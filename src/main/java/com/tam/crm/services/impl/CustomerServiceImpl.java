package com.tam.crm.services.impl;

import com.tam.crm.daos.CustomerDao;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import com.tam.crm.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Autowired
	CustomerDao dao;

	@Override
	public List<Customer> getCustomers() {
		return dao.selectCustomers();
	}

	@Override
	public Customer getCustomer(Long id) {
		return dao.findCustomerById(id);
	}

	@Override
	public void updateCustomer(Long id, UpdateCustomer customer) {
		dao.updateCustomer(id, customer);
	}

	@Override
	public void deleteCustomer(Long id) {
		dao.deleteCustomer(id);
	}

	@Override
	public byte[] getPhotoCustomer(Long id) {
		return dao.selectPhotoCustomer(id);
	}

	@Override
	public void updatePhotoCustomer(Long id, MultipartFile file) {
		try {
			checkFile(file);
			dao.updatePhoto(id,file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkFile(MultipartFile file) {

	}
}
