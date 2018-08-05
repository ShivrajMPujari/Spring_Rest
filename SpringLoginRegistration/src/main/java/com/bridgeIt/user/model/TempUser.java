package com.bridgeIt.user.model;

import java.sql.Date;

public class TempUser {

	private String uuid;
	private String email;

	private java.sql.Timestamp startingInterval;
	private java.sql.Timestamp endingInterval;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public java.sql.Timestamp getStartingInterval() {
		return startingInterval;
	}
	public void setStartingInterval(java.sql.Timestamp startingInterval) {
		this.startingInterval = startingInterval;
	}
	public java.sql.Timestamp getEndingInterval() {
		return endingInterval;
	}
	public void setEndingInterval(java.sql.Timestamp endingInterval) {
		this.endingInterval = endingInterval;
	}

	
	
	
}
