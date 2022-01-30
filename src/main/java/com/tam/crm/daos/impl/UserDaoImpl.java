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
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Override public List<User> getUsers() {
		return jdbcTemplate.query("SELECT *  FROM crmuser where deleted = false", BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public Long createUser(NewUser user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue("username",user.getUsername())
			.addValue("admin",user.isAdmin())
			.addValue("email",user.getEmail());
		return namedParameterJdbcTemplate.queryForObject("INSERT INTO crmuser ( username,email,admin ) VALUES ( :username,:email,:admin) RETURNING id", paramSource, Long.class);

	}

	@Override public void update(Long id, UpdateUser user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue("admin",user.isAdmin())
			.addValue("email",user.getEmail())
			.addValue("id",id);
		namedParameterJdbcTemplate.update("update crmuser  set  email=:email,admin=:admin where id=:id", paramSource);
	}

	@Override public void deleteUser(Long id) {

		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("id",id);
		namedParameterJdbcTemplate.update("update crmuser set deleted=true where id=:id", paramSource);

	}

	@Override public void setAdmin(Long id, boolean status) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource()
			.addValue("admin",status)
			.addValue("id",id);
		namedParameterJdbcTemplate.update("update crmuser  set  admin=:admin where id=:id", paramSource);
	}

	@Override public User getUser(Long id) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("id",id);
		return namedParameterJdbcTemplate.queryForObject("select * from crmuser where id=:id and deleted=false", paramSource, BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public User getUserByLogin(String username) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("username",username);
		return namedParameterJdbcTemplate.queryForObject("select * from crmuser where username=:username and deleted=false", paramSource, BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public List<String> getAdminEmails() {
		return jdbcTemplate.queryForList("select email from crmuser where admin=true and deleted=false",String.class);
	}
}
