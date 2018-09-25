package com.bridgeIt.user;

import org.springframework.http.HttpStatus;

import com.bridgeIt.user.model.Contract;

public class ContractResponse {


	private HttpStatus status;
	private int code;
	private Contract contract;
	private String message;
	
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

	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
