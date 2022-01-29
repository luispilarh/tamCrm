package com.tam.crm.controllers;

import com.tam.crm.exception.CrmDataException;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import com.tam.crm.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Users")
@RestController
@RequestMapping(value = "v1/users")
public class UserController {

	@Autowired
	private UserService service;

	@GetMapping()
	public List<User> getUsers() {
		return service.getUsers();
	}

	@PostMapping()
	public User createUser(@RequestBody NewUser user) throws CrmDataException {
		return service.createUser(user);
	}

	@PutMapping("{id}")
	public void updateUser(@PathVariable Long id, @RequestBody UpdateUser user) throws CrmDataException {
		service.updateUser(id, user);
	}

	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable Long id) throws CrmDataException {
		service.deleteUser(id);
	}

	@PutMapping("{id}/admin/{status}")
	public void setAdminStatus(@PathVariable Long id, @PathVariable boolean status) throws CrmDataException {
		service.setAdminStatus(id, status);
	}
}
