package com.tam.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class Customer extends UpdateCustomer {
	Long id;
	String photo;
	String lasUpdatedBy;

	@JsonIgnore
	public int getUniqeCode() {
		if (name == null && surname == null) {
			return -1;
		} else if (name != null && surname == null) {
			return name.hashCode();
		} else if (name == null) {
			return surname.hashCode();
		}
		return (name + "%%" + surname).hashCode();
	}
}
