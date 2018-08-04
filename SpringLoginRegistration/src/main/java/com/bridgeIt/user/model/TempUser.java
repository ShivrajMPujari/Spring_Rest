package com.bridgeIt.user.model;

import java.sql.Date;

public class TempUser {

	private String uuid;
	private String email;
	private Date startingInterval;
	private Date endingInterval;
	
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
	public Date getStartingInterval() {
		return startingInterval;
	}
	public void setStartingInterval(Date startingInterval) {
		this.startingInterval = startingInterval;
	}
	public Date getEndingInterval() {
		return endingInterval;
	}
	public void setEndingInterval(Date endingInterval) {
		this.endingInterval = endingInterval;
	}
	
	
	
}
