package com.tam.crm.services.impl;

import com.tam.crm.daos.UserDao;
import com.tam.crm.exception.CrmDataException;
import com.tam.crm.exception.UnregisteredUserException;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import com.tam.crm.services.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
	private static final String CACHE_USERS = "users";
	@Autowired
	private UserDao dao;
	@Autowired
	private CacheManager cacheManager;

	@Override
	public List<User> getUsers() {
		return dao.getUsers();
	}

	@Override
	public User createUser(NewUser user) throws CrmDataException {
		try {
			Long id = dao.createUser(user);
			User ret = new User();
			BeanUtils.copyProperties(user, ret);
			ret.setId(id);
			return ret;
		} catch (DataIntegrityViolationException e) {
			throw new CrmDataException("User creation failed. " + e.getMessage());
		}
	}

	@Override
	public void updateUser(Long id, UpdateUser user) throws CrmDataException {
		evictCache(id);
		dao.update(id, user);
	}

	@Override
	public void deleteUser(Long id) throws CrmDataException {
		evictCache(id);
		dao.deleteUser(id);
	}

	private void evictCache(Long id) throws CrmDataException {
		try {
			User user = dao.getUser(id);
			Cache cache = cacheManager.getCache(CACHE_USERS);
			if (cache != null)
				cache.evictIfPresent(user.getUsername());
		} catch (DataRetrievalFailureException e) {
			throw new CrmDataException("User not found");
		}

	}

	@Override public void setAdminStatus(Long id, boolean status) throws CrmDataException {
		evictCache(id);
		dao.setAdmin(id, status);
	}

	@Override public boolean isAdmin(Long id) {

		User user = dao.getUser(id);
		return user.isAdmin();
	}

	@Override
	@Cacheable(cacheNames = CACHE_USERS)
	public User getUser(String login) throws UnregisteredUserException {
		try {
			return dao.getUserByLogin(login);
		} catch (DataRetrievalFailureException e) {
			throw new UnregisteredUserException("User not found");
		}
	}

	@Override
	public Map<String, Cache> getCache() {
		Map<String, Cache> ret = new HashMap<>();
		for (String name : cacheManager.getCacheNames()) {
			ret.put(name, cacheManager.getCache(name));
		}
		return ret;
	}
}
