package com.bridgeIt.user.model;

public class UserAccount {
	
	private String accountNumber;
	private int balance;
	private String accountHolderName;
	private String bank;
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public String getAccountHolderName() {
		return accountHolderName;
	}
	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	@Override
	public String toString() {
		return "UserAccount [accountNumber=" + accountNumber + ", balance=" + balance + ", accountHolderName="
				+ accountHolderName + ", bank=" + bank + "]";
	}
}
