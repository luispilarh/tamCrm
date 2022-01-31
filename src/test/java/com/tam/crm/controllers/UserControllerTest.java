package com.tam.crm.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tam.crm.TestConfiguration;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.services.CustomerService;
import com.tam.crm.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
@ContextConfiguration(classes = TestConfiguration.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getUsers() throws Exception {
		Mockito.when(userService.getUsers()).thenReturn(List.of());
		RequestBuilder request = MockMvcRequestBuilders
			.get("/v1/users");
		MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
		Assertions.assertNotNull(mvcResult.getResponse().getContentAsString());

	}

	@Test
	void createUser() throws Exception {
		NewUser newUser = new NewUser();
		newUser.setEmail("email");
		newUser.setUsername("username");
		newUser.setAdmin(false);
		RequestBuilder request = MockMvcRequestBuilders
			.post("/v1/users/")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(newUser));
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void updateUser() throws Exception {
		UpdateUser user = new UpdateUser();
		user.setEmail("email");
		user.setAdmin(false);
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/users/{id}",1l)
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(user));
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void deleteUser() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.delete("/v1/users/{id}",1l);
		mockMvc.perform(request).andExpect(status().isOk());

	}

	@Test
	void setAdminStatus() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.put("/v1/users/{id}/admin/{status}",1l,false);
		mockMvc.perform(request).andExpect(status().isOk());
	}
}