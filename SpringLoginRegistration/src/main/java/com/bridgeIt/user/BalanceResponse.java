package com.bridgeIt.user;

public class BalanceResponse {

	private String accountNumber;
	private int Balance;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public int getBalance() {
		return Balance;
	}
	public void setBalance(int balance) {
		Balance = balance;
	}
	@Override
	public String toString() {
		return "BalanceResponse [accountNumber=" + accountNumber + ", Balance=" + Balance + "]";
	}
	
}
