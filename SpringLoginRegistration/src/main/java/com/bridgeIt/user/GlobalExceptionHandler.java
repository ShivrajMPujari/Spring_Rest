package com.bridgeIt.user;

import java.sql.SQLDataException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(value = SQLDataException.class)
	public ResponseEntity<BaseResponse> handleNullPoiException(){
		
		BaseResponse response = new BaseResponse();
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setMessage(" exception occured while running query....");
		return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = NullPointerException.class)
	public ResponseEntity<BaseResponse> handleNullPointerException(){
		
		BaseResponse response = new BaseResponse();
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setMessage("Null poniter exception....");
		return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
	}
	
	
	
	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<BaseResponse> handleDataAccessException(){
		
		BaseResponse response = new BaseResponse();
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setMessage("Something went wrong at run time....");
		return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
	}

	
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<BaseResponse> handleException(Exception e){
		
		System.out.println(e.getMessage());
		
		BaseResponse response = new BaseResponse();
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setMessage("Something went wrong....");
		return new ResponseEntity<BaseResponse>(response,HttpStatus.BAD_REQUEST);
	}
	
	
	
}
