package com.tam.crm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UpdateCustomer {
	String name;
	String surname;
	String email;
}
