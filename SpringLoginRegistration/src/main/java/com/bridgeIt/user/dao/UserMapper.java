package com.bridgeIt.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.bridgeIt.user.model.User;

public class UserMapper implements RowMapper<User>{

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setCity(rs.getString("city"));
		user.setEmail(rs.getString("email"));
		user.setMobileNo(rs.getString("mobileNo"));
		user.setRole(rs.getString("role"));
		user.setPassword(rs.getString("password"));
		user.setVerified(rs.getBoolean("verified"));
		user.setAuthenticatedUserKey(rs.getString("authenticated_user_key"));
		user.setBalance(rs.getInt("balance"));
		user.setBank(rs.getString("bank"));
		user.setAccounNumber(rs.getString("account_number"));
		user.setUserAccount(rs.getBytes("user_account"));
		return user;
	}

}
