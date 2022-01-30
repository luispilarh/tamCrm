package com.tam.crm.services;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import org.springframework.cache.Cache;

import java.util.List;
import java.util.Map;

public interface UserService {
	List<User> getUsers();

	User createUser(NewUser user) throws CrmDataException;

	void updateUser(Long id, UpdateUser user) throws CrmDataException;

	void deleteUser(Long id) throws CrmDataException;

	void setAdminStatus(Long id, boolean status) throws CrmDataException;

	boolean isAdmin(Long id);

	User getUser(String gitHubLogin) throws UnregisteredUserException;

	List<String> getAdminEmails() throws CrmDataException;
}
