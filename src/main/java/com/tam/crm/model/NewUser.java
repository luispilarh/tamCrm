package com.tam.crm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewUser extends UpdateUser{
	private String username;
}
