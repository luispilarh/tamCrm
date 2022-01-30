package com.tam.crm.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.daos.CustomerDao;
import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.services.CsvService;
import com.tam.crm.services.StorageService;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.reader.NamedCsvRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvServiceImpl implements CsvService {
	@Autowired
	StorageService storageService;

	@Value("${csv.separator}")
	char separator;
	@Value("${csv.quote}")
	char quote;
	@Value("${csv.header}")
	String[] fields;
	@Autowired
	ObjectMapper objectMapper;

	@Autowired CustomerDao customerDao;

	@Override
	public void process(List<ResultCSV> result, List<Customer> toInsert, Map<Integer, Long> inserted, String key) {
		InputStreamReader inputStreamReader = new InputStreamReader(storageService.getObject(StorageServiceImpl.BUCKET_CSV, key).getObjectContent());
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
			message = message + "Photo is requiered || ";
		} else if (!storageService.exitsObject(customer.getPhoto())) {
			level = ResultCSV.Level.WARN;
			message = message + "Photo not found in s3 || ";
		}
		if (!StringUtils.hasText(customer.getName())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Name is requiered || ";
		}
		if (!StringUtils.hasText(customer.getSurname())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Surname is requiered || ";
		}
		if (StringUtils.hasText(customer.getSurname()) && StringUtils.hasText(customer.getName()) && inserted.get(customer.getUniqeCode()) != null) {
			level = ResultCSV.Level.ERROR;
			message = message + "Customer duplicated in line " + inserted.get(customer.getUniqeCode()) + " || ";
		} else if (StringUtils.hasText(customer.getSurname()) && StringUtils.hasText(customer.getName()) && customerDao.existCustomer(customer.getName(), customer.getSurname())) {
			level = ResultCSV.Level.ERROR;
			message = message + "Customer duplicated in bbdd || ";
		}
		return new ResultCSV(lineNumber, level, message);
	}
}
