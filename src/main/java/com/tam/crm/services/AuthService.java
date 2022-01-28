package com.tam.crm.services;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;

public interface AuthService {
	User getCurrentUser() throws UnregisteredUserException;
}
