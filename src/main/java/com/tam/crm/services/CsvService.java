package com.tam.crm.services;

import com.tam.crm.model.Customer;
import com.tam.crm.model.ResultCSV;

import java.util.List;
import java.util.Map;

public interface CsvService {
	void process(List<ResultCSV> result, List<Customer> toInsert, Map<Integer, Long> inserted, String key);
}
