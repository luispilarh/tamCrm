package com.tam.crm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UpdateUser {
	private boolean admin;
	private String email;
}
