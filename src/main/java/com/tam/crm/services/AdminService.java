package com.tam.crm.services;

import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;

import java.util.List;

public interface AdminService {
	List<User> getUsers();

	User createUser(User user);

	void updateUser(User user);

	void deleteUser(Long id);

	void setAdminStatus(boolean status);

	boolean isAdmin(String id);

	User getUser(String gitHubLogin) throws UnregisteredUserException;
}
