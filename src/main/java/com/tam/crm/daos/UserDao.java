package com.tam.crm.daos;

import com.tam.crm.model.User;

import java.util.List;

public interface UserDao {
	List<User> getUsers();

	User createUser(User user);

	void update(User user);

	void deleteUser(Long id);

	void setAdmin(Long id, boolean status);

	User getUser(Long id);

	User getUserByLogin(String login);
}
