package com.tam.crm.model;

import lombok.Data;

@Data
public class User {
	private String login;
	private boolean admin;
	private Integer id;
}
