package com.tam.crm.services.impl;

import com.tam.crm.daos.UserDao;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.User;
import com.tam.crm.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
	@Autowired
	private UserDao dao;

	@Override
	public List<User> getUsers() {
		return dao.getUsers();
	}

	@Override
	public User createUser(User user) {
		return dao.createUser(user);
	}

	@Override
	public void updateUser(User user) {
		dao.update(user);
	}

	@Override
	public void deleteUser(Long id) {
		dao.deleteUser(id);
	}

	@Override public void setAdminStatus(Long id, boolean status) {
		dao.setAdmin(id,status);
	}

	@Override public boolean isAdmin(Long id) {

		User user = dao.getUser(id);
		return user.isAdmin();
	}

	@Override public User getUser(String gitHubLogin) throws UnregisteredUserException {
		return dao.getUserByLogin(gitHubLogin);
	}
}
