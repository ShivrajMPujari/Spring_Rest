package com.bridgeIt.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bridgeIt.user.model.TradeAdmin;


public class TradeAdminMapper implements RowMapper<TradeAdmin> {

	@Override
	public TradeAdmin mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		TradeAdmin admin = new TradeAdmin();
		admin.setId(rs.getInt("id"));
		admin.setName(rs.getString("name"));
	//	admin.setData(rs.getBlob("account_data").getBytes(pos, length));
		
		return null;
	}


	
	
	
}
