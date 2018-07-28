package com.bridgeIt.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class BaseResponse {

	private HttpStatus code;
	private String status;
	
	public HttpStatus getCode() {
		return code;
	}
	public void setCode(HttpStatus ok) {
		this.code = ok;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
