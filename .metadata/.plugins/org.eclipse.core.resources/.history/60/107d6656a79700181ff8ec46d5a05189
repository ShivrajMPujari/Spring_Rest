package com.bridgeIt.user;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bridgeIt.user.model.User;

@Component
@Scope(value="prototype")
public class BaseResponse {
	
	
	private HttpStatus status;
	private String message;
	private List errors;
	private User user;
	
	public HttpStatus getStatus() {
		return status;
	}
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List getErrors() {
		return errors;
	}
	public void setErrors(List errors) {
		this.errors = errors;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
