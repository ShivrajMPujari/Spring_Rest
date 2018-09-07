package com.bridgeIt.user.model;

import java.util.List;

import org.springframework.http.HttpStatus;

public class ContractLists {
	
	private List<Contract> contracts;

	private HttpStatus status;
	private int code;
	
	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	
}
