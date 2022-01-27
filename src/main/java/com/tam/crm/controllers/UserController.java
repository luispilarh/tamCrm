package com.tam.crm.controllers;

import com.tam.crm.model.User;
import com.tam.crm.services.AdminService;
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

@Api(tags = "Admin")
@RestController
@RequestMapping(value = "v1/admin/users")
public class UserController {

	@Autowired
	private AdminService service;

	@GetMapping()
	public List<User> getUsers() {
		return service.getUsers();
	}

	@PostMapping()
	public User createUser(@RequestBody User user) {
		return service.createUser(user);
	}

	@PutMapping()
	public void updateUser(@RequestBody User user) {
		service.updateUser(user);
	}

	@DeleteMapping("{id}")
	public void deleteUser(@PathVariable Long id) {
		service.deleteUser(id);
	}

	@PutMapping("{id}/admin/{status}")
	public void setAdminStatus(@PathVariable Long id,@PathVariable boolean status) {
		service.setAdminStatus(id,status);
	}
}
