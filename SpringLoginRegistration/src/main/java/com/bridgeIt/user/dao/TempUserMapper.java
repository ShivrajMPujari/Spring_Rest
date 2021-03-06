package com.bridgeIt.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bridgeIt.user.model.TempUser;

public class TempUserMapper  implements RowMapper<TempUser>{

	@Override
	public TempUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		TempUser tempUser = new TempUser();
		tempUser.setEmail(rs.getString("email"));
		tempUser.setStartingInterval(rs.getTimestamp("starting_interval"));
		tempUser.setEndingInterval(rs.getTimestamp("ending_interval"));
		tempUser.setUuid(rs.getString("authenticated_user_key"));

		
		return tempUser;
	}
	
}
