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
	CustomerDao dao;
	@Autowired
	StorageService storageService;
	@Autowired
	AuthService authService;
	@Value("${csv.separator}")
	char separator;
	@Value("${csv.quote}")
	char quote;
	@Value("${csv.header}")
	String[] fields;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	EmailService emailService;

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
	public List<ResultCSV> processCSV(MultipartFile file) throws CrmDataException {
		InputStreamReader inputStreamReader = null;
		List<ResultCSV> result = new ArrayList<>();
		List<Customer> toInsert = new ArrayList<>();
		Map<Integer, Long> inserted = new HashMap<>();
		try {
			User currentUser = authService.getCurrentUser();
			String key = storageService.putObject(StorageServiceImpl.BUCKET_CSV, currentUser.getId(), file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
			inputStreamReader = new InputStreamReader(storageService.getObject(StorageServiceImpl.BUCKET_CSV, key).getObjectContent());
			NamedCsvReader.builder()
				.fieldSeparator(separator)
				.quoteCharacter('"')
				.build(inputStreamReader)
				.forEach(namedCsvRow -> {
					Customer customer = extractCustomer(namedCsvRow);
					ResultCSV resultCSV = validateCustomer(customer, namedCsvRow.getOriginalLineNumber(), inserted);
					if (!resultCSV.getLevel().equals(ResultCSV.Level.ERROR)) {
						toInsert.add(customer);
						inserted.put(customer.getUniqeCode(), namedCsvRow.getOriginalLineNumber());
					}
					if (!resultCSV.getLevel().equals(ResultCSV.Level.INFO))
						result.add(resultCSV);
				});
			int[] ints = dao.insertBatch(toInsert, currentUser.getId());
			emailService.sendCSVResult(result, toInsert.size(), ints.length, currentUser, key);

		} catch (UnregisteredUserException e) {
			throw new CrmDataException("fail to create customers, user not found.");
		} catch (IOException e) {
			throw new CrmDataException("fail to create customers");
		}
		return result;
	}

	private Customer extractCustomer(NamedCsvRow namedCsvRow) {
		Map<String, String> mapCustomer = new HashMap<>();
		for (String name : fields) {
			mapCustomer.put(name, namedCsvRow.getField(name));
		}
		return objectMapper.convertValue(mapCustomer, Customer.class);

	}

	private ResultCSV validateCustomer(Customer customer, long lineNumber, Map<Integer, Long> inserted) {
		ResultCSV.Level level = ResultCSV.Level.INFO;
		String message = "";
		if (!StringUtils.hasText(customer.getPhoto())) {
			level = ResultCSV.Level.WARN;
			message = message + "Photo is requiered\n";
		} else if (!storageService.exitsObject(customer.getPhoto())) {
			level = ResultCSV.Level.WARN;
			message = message + "Photo not found in s3\n";
		}
		if (!StringUtils.hasText(customer.getName())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Name is requiered\n";
		}
		if (!StringUtils.hasText(customer.getSurname())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Surname is requiered\n";
		}
		if (StringUtils.hasText(customer.getSurname()) && StringUtils.hasText(customer.getName()) && inserted.get(customer.getUniqeCode()) != null) {
			level = ResultCSV.Level.ERROR;
			message = message + "Customer duplicated in line " + inserted.get(customer.getUniqeCode()) + "\n";
		} else if (StringUtils.hasText(customer.getSurname()) && StringUtils.hasText(customer.getName()) && dao.existCustomer(customer.getName(), customer.getSurname())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Customer duplicated in bbdd\n";
		}
		return new ResultCSV(lineNumber, level, message);
	}

}
