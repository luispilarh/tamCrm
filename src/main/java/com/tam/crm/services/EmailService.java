package com.tam.crm.services;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.ResultCSV;
import com.tam.crm.model.User;

import java.io.IOException;
import java.util.List;

public interface EmailService {

	void sendCSVResult(List<ResultCSV> result, int size, int length, User currentUser, String key) throws CrmDataException;

	String createBody(List<ResultCSV> result, int toInsert, int inserted, User currentUser, String key) throws IOException;
}
