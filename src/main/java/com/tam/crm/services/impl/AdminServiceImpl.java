package com.tam.crm.services.impl;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.AdminService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
	@Override public List<User> getUsers() {
		return null;
	}

	@Override public User createUser(User user) {
		return null;
	}

	@Override public void updateUser(User user) {

	}

	@Override public void deleteUser(Long id) {

	}

	@Override public void setAdminStatus(boolean status) {

	}

	@Override public boolean isAdmin(String id) {
		return false;
	}

	@Override public User getUser(String gitHubLogin) throws UnregisteredUserException {
		throw new UnregisteredUserException("user not found");
	}
}
