package com.bridgeIt.mvcDemo.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND , reason="Employee not found")
public class EmployeeNotFound extends Exception{

	public EmployeeNotFound(int id) {
		
		super("Employee not found  with id:- "+id);
	}
}
