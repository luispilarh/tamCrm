package com.tam.crm.services.impl;

import com.tam.crm.daos.UserDao;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application.yml")
class UserServiceImplTest {

	@InjectMocks
	private UserServiceImpl service;
	@Mock
	private UserDao dao;
	@Mock
	private CacheManager cacheManager;

	@Test
	void getUsers() {
		User user = new User();
		List<User> list = List.of(user);
		Mockito.when(dao.getUsers()).thenReturn(list);
		Assertions.assertEquals(list, service.getUsers());
	}

	@Test
	void createUser() throws CrmDataException {
		NewUser newUser = new User();
		long newUserId = 6l;
		Mockito.when(dao.createUser(newUser)).thenReturn(newUserId);
		Assertions.assertEquals(service.createUser(newUser).getId(), newUserId);
	}

	@Test
	void createUserFail() {
		NewUser newUser = new User();
		Mockito.when(dao.createUser(newUser)).thenThrow(Mockito.mock(DataAccessException.class));
		Assertions.assertThrows(CrmDataException.class, () -> service.createUser(newUser));
	}

	@Test
	void updateUserFail() {
		long userId = 5l;
		Mockito.when(dao.getUser(userId)).thenThrow(Mockito.mock(DataAccessException.class));
		Assertions.assertThrows(CrmDataException.class, () -> service.updateUser(userId, new UpdateUser()));
	}

	@Test
	void updateUser() throws CrmDataException {
		long userId = 5l;
		mockCacheEvict(userId);
		UpdateUser user = new UpdateUser();
		service.updateUser(userId, user);
		Mockito.verify(dao, Mockito.times(1)).update(userId, user);
	}

	@Test
	void deleteUser() throws CrmDataException {
		long userId = 5l;
		service.deleteUser(userId);
		Mockito.verify(dao, Mockito.times(1)).deleteUser(userId);
	}

	@Test
	void setAdminStatus() throws CrmDataException {
		long userId = 5l;
		boolean status = true;
		service.setAdminStatus(userId, status);
		Mockito.verify(dao, Mockito.times(1)).setAdmin(userId, status);
	}

	@Test
	void isAdmin() {
		long id = 1l;
		User user = new User();
		user.setAdmin(false);
		Mockito.when(dao.getUser(id)).thenReturn(user);
		Assertions.assertFalse(service.isAdmin(id));
	}

	@Test
	void getUser() throws UnregisteredUserException {
		String username = "user";
		User user = new User();
		Mockito.when(dao.getUserByLogin(username)).thenReturn(user);
		Assertions.assertEquals(service.getUser(username), user);

	}

	@Test
	void getUserFail() {
		String username = "user";
		Mockito.when(dao.getUserByLogin(username)).thenThrow(Mockito.mock(DataAccessException.class));
		Assertions.assertThrows(UnregisteredUserException.class, () -> service.getUser(username));

	}

	@Test
	void getAdminEmails() throws CrmDataException {
		List<String> mails = List.of("mail1","mail2");
		Mockito.when(dao.getAdminEmails()).thenReturn(mails);
		Assertions.assertEquals(service.getAdminEmails(),mails);
	}
	@Test
	void getAdminEmailsFail() {
		Mockito.when(dao.getAdminEmails()).thenThrow(Mockito.mock(DataAccessException.class));
		Assertions.assertThrows(CrmDataException.class, () ->service.getAdminEmails());
	}

	private void mockCacheEvict(long userId) {
		Mockito.when(dao.getUser(userId)).thenReturn(new User());
		Mockito.when(cacheManager.getCache(UserServiceImpl.CACHE_USERS)).thenReturn(Mockito.mock(org.springframework.cache.Cache.class));
	}
}