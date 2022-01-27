package com.tam.crm.daos.impl;

import com.tam.crm.daos.UserDao;
import com.tam.crm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDaoImpl implements UserDao {
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Override public List<User> getUsers() {
		return jdbcTemplate.query("SELECT *  FROM public.\"user\"", BeanPropertyRowMapper.newInstance(User.class));
	}

	@Override public User createUser(User user) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("login",user.getLogin());
		Long id = namedParameterJdbcTemplate.queryForObject("INSERT INTO public.\"user\" ( login) VALUES ( :login) RETURNING id", paramSource, Long.class);
		return getUser(id);
	}

	@Override public void update(User user) {

	}

	@Override public void deleteUser(Long id) {

		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("id",id);
		namedParameterJdbcTemplate.update("delete from public.\"user\" where id=:id", paramSource);

	}

	@Override public void setAdmin(Long id, boolean status) {

	}

	@Override public User getUser(Long id) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("id",id);
		return namedParameterJdbcTemplate.queryForObject("select * from public.\"user\" where id=:id", paramSource, User.class);
	}

	@Override public User getUserByLogin(String login) {
		return null;
	}
}
