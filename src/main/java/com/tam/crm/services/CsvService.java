package com.tam.crm.services;

import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;

import java.util.List;

public interface CsvService {
	void process(List<ResultCSV> result, List<Customer> toInsert, String key);
}
