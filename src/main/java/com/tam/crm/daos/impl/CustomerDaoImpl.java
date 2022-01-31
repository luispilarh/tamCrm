package com.tam.crm.daos.impl;

import com.tam.crm.daos.CustomerDao;
import com.tam.crm.model.Customer;
import com.tam.crm.model.UpdateCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerDaoImpl implements CustomerDao {
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String USER_ID = "userId";
	private static final String SURNAME = "surname";
	private static final String EMAIL = "email";
	private static final String PHOTO = "photo";
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public List<Customer> selectCustomers() {
		return jdbcTemplate.query("select * from customer where deleted=false", BeanPropertyRowMapper.newInstance(Customer.class));
	}

	@Override
	public Customer findCustomerById(Long id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
		return namedParameterJdbcTemplate.queryForObject("select customer.*,username as lasUpdatedBy from customer "
				+ " join crmuser on crmuser.id=customer.userId"
				+ " where customer.id=:id and customer.deleted=false", parameters,
			BeanPropertyRowMapper.newInstance(Customer.class));
	}

	@Override public int updateCustomer(Long id, UpdateCustomer customer, Long userId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue(ID, id)
			.addValue(NAME, customer.getName())
			.addValue(USER_ID, userId)
			.addValue(SURNAME, customer.getSurname())
			.addValue(EMAIL, customer.getEmail());
		return namedParameterJdbcTemplate.update("update customer set name=:name,surname=:surname,email=:email,userId=:userId  where id=:id and deleted=false", parameters);

	}

	@Override public int deleteCustomer(Long id, Long userId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue(ID, id)
			.addValue(USER_ID, userId);
		return namedParameterJdbcTemplate.update("update customer set deleted=true,userId=:userId where id=:id and deleted=false", parameters);
	}

	@Override
	public int updatePhoto(Long id, String photo, Long userId) {
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			.addValue(ID, id)
			.addValue(USER_ID, userId)
			.addValue(PHOTO, photo);
		return namedParameterJdbcTemplate.update("update customer set photo=:photo,userId=:userId where id=:id and deleted=false", parameters);
	}

	@Override
	public int[] insertBatch(List<Customer> toInsert, Long userId) {
		String sql = "INSERT INTO public.customer ( name, surname, email, photo, userid) "
			+ "VALUES ( ?, ?, ?, ?, ? );\n";
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override public void setValues(PreparedStatement ps, int i) throws SQLException {
				Customer customer = toInsert.get(i);
				ps.setString(1, customer.getName());
				ps.setString(2, customer.getSurname());
				ps.setString(3, customer.getEmail());
				ps.setString(4, customer.getPhoto());
				ps.setLong(5, userId );
			}

			@Override public int getBatchSize() {
				return toInsert.size();
			}
		});
	}

	@Override
	public boolean existCustomer(String name, String surname) {

		MapSqlParameterSource parameterSource = new MapSqlParameterSource()
			.addValue(NAME, name)
			.addValue(SURNAME, surname);
		return namedParameterJdbcTemplate.queryForObject("select exists( select id from customer where name=:name and surname=:surname)", parameterSource, Boolean.class);
	}

}
