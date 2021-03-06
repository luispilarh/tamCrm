package com.tam.crm.daos.impl;

import com.tam.crm.daos.UserDao;
import com.tam.crm.model.NewUser;
import com.tam.crm.model.UpdateUser;
import com.tam.crm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDaoImpl implements UserDao {
	private static final String PARAM_USERNAME = "username";
	private static final String PARAM_ADMIN = "admin";
	private static final String PARAM_EMAIL = "email";
	private static final String PARAM_ID = "id";
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Override public List<User> getUsers() {
		return jdbcTemplate.query("SELECT *  FROM crmuser where deleted = false", BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public Long createUser(NewUser user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue(PARAM_USERNAME,user.getUsername())
			.addValue(PARAM_ADMIN,user.isAdmin())
			.addValue(PARAM_EMAIL,user.getEmail());
		return namedParameterJdbcTemplate.queryForObject("INSERT INTO crmuser ( username,email,admin ) VALUES ( :username,:email,:admin) RETURNING id", paramSource, Long.class);

	}

	@Override public void update(Long id, UpdateUser user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue(PARAM_ADMIN,user.isAdmin())
			.addValue(PARAM_EMAIL,user.getEmail())
			.addValue(PARAM_ID,id);
		namedParameterJdbcTemplate.update("update crmuser  set  email=:email,admin=:admin where id=:id", paramSource);
	}

	@Override public void deleteUser(Long id) {

		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue(PARAM_ID,id);
		namedParameterJdbcTemplate.update("update crmuser set deleted=true where id=:id", paramSource);

	}

	@Override public void setAdmin(Long id, boolean status) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue(PARAM_ADMIN,status)
			.addValue(PARAM_ID,id);
		namedParameterJdbcTemplate.update("update crmuser  set  admin=:admin where id=:id", paramSource);
	}

	@Override public User getUser(Long id) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue(PARAM_ID,id);
		return namedParameterJdbcTemplate.queryForObject("select * from crmuser where id=:id and deleted=false", paramSource, BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public User getUserByLogin(String username) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue(PARAM_USERNAME,username);
		return namedParameterJdbcTemplate.queryForObject("select * from crmuser where username=:username and deleted=false", paramSource, BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public List<String> getAdminEmails() {
		return jdbcTemplate.queryForList("select email from crmuser where admin=true and deleted=false",String.class);
	}
}
