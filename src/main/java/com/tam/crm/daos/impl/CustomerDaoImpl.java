package com.tam.crm.daos.impl;

import com.tam.crm.daos.CustomerDao;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerDaoImpl implements CustomerDao {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<Customer> selectCustomers() {
		return jdbcTemplate.query("select * from customer", BeanPropertyRowMapper.newInstance(Customer.class));
	}

	@Override
	public Customer findCustomerById(Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject("select * from customer where id=:id", parameters,
			BeanPropertyRowMapper.newInstance(Customer.class));
	}

	@Override public void updateCustomer(Long id, UpdateCustomer customer) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("name", customer.getName())
			.addValue("surname", customer.getSurname())
			.addValue("email", customer.getEmail());
		namedParameterJdbcTemplate.update("update customer set name=:name,surname=:surname,email=:email where id=:id", parameters);

	}

	@Override public void deleteCustomer(Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("id", id);
		namedParameterJdbcTemplate.update("delete from customer where id=:id", parameters);
	}

	@Override
	public void updatePhoto(Long id, byte[] bytes) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue("id", id)
			.addValue("photo", bytes);
		namedParameterJdbcTemplate.update("update customer set photo=:photo customer where id=:id", parameters);
	}

	@Override public byte[] selectPhotoCustomer(Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject("select photo from customer where id=:id", parameters, byte[].class);
	}
}
