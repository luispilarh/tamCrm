package com.tam.crm.daos.impl;

import com.tam.crm.TestConfiguration;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@JdbcTest
@Import(UserDaoImpl.class)
@ContextConfiguration(classes = TestConfiguration.class)
class UserDaoImplTest {

	@Autowired
	UserDaoImpl dao;

	@Test
	void getUsers() {
		Assertions.assertNotNull(dao.getUsers());
	}

	//@Test
	//h2 not suport returnig id
	void createUser() {
		int size = dao.getUsers().size();
		NewUser user = new NewUser();
		user.setUsername("juan");
		user.setAdmin(false);
		user.setEmail("m@m.com");
		dao.createUser(user);
		Assertions.assertEquals(size + 1, dao.getUsers().size());
	}

	@Test
	void update() {
		User user = dao.getUser(1l);
		UpdateUser user1 = new UpdateUser();
		user1.setEmail("newEmail@m.com");
		dao.update(1l, user1);
		Assertions.assertNotEquals(user, dao.getUser(1l));
	}

	@Test
	void deleteUser() {
		List<User> users = dao.getUsers();
		dao.deleteUser(1l);
		Assertions.assertNotEquals(users.size(), dao.getUsers().size());
	}

	@Test
	void setAdmin() {
		User user = dao.getUser(1l);
		dao.setAdmin(1l, false);
		Assertions.assertNotEquals(user, dao.getUser(1l));
	}

	@Test
	void getUser() {
		User user = dao.getUser(1l);
		Assertions.assertNotNull(user);
		Assertions.assertEquals(user.getUsername(), "pepe");
		Assertions.assertEquals(user.getEmail(), "mail@gmail.com");
	}

	@Test
	void getUserByLogin() {

		Assertions.assertEquals(dao.getUser(1l), dao.getUserByLogin("pepe"));
	}

	@Test
	void getAdminEmails() {
		List<String> adminEmails = dao.getAdminEmails();
		Assertions.assertNotNull(adminEmails);
		Assertions.assertEquals(adminEmails.size(),1);
	}
}