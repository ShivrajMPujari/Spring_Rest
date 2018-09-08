package com.bridgeIt.user.model;

import java.util.Arrays;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

//@JsonIgnoreProperties({"password","userAccount"})
public class User {

	private int id;
	
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
	
	@JsonIgnore
	private String password;
	
	private boolean verified;
	
	private String authenticatedUserKey;
	

	private String accounNumber;
	
	private int balance;
	
	@NotEmpty(message="Bank name  is required")
	private String bank;
	
	@JsonIgnore
	private byte[] userAccount;
	
	public String getAuthenticatedUserKey() {
		return authenticatedUserKey;
	}
	public void setAuthenticatedUserKey(String authenticatedUserKey) {
		this.authenticatedUserKey = authenticatedUserKey;
	}
	

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
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccounNumber() {
		return accounNumber;
	}
	public void setAccounNumber(String accounNumber) {
		this.accounNumber = accounNumber;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	
	public byte[] getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(byte[] userAccount) {
		this.userAccount = userAccount;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", role=" + role + ", email=" + email + ", city=" + city
				+ ", mobileNo=" + mobileNo + ", password=" + password + ", verified=" + verified
				+ ", authenticatedUserKey=" + authenticatedUserKey + ", accounNumber=" + accounNumber + ", balance="
				+ balance + ", bank=" + bank + ", userAccount=" + Arrays.toString(userAccount) + "]";
	}



}
