package com.bridgeIt.user.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class User {

	@NotEmpty(message="name is required")
	private String name;
	
	@NotEmpty(message="role is needed to be filled")
	private String role;
	
	@NotEmpty(message="email is required")
	@Email(message="email is invalid")
	private String email;
	
	@NotEmpty(message="city is required")
	private String city;
	
	@NotEmpty(message="mobile no. is required")
	private String mobileNo;
	private String password;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	@Override
	public String toString() {
		return "User [name=" + name + ", role=" + role + ", email=" + email + ", city=" + city + ", mobileNo="
				+ mobileNo + ", password=" + password + "]";
	}

}
