package com.tam.crm.daos;

import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;

import java.util.List;

public interface UserDao {
	List<User> getUsers();

	Long createUser(NewUser user);

	void update(Long id, UpdateUser user);

	void deleteUser(Long id);

	void setAdmin(Long id, boolean status);

	User getUser(Long id);

	User getUserByLogin(String login);
}
