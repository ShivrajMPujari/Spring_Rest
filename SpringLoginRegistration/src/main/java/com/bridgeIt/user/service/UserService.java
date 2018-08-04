package com.bridgeIt.user.service;

import com.bridgeIt.user.BaseResponse;
import com.bridgeIt.user.model.User;

public interface UserService {

	BaseResponse userReg(User user);
	boolean login(String mail, String password);
	boolean verify(String token);
	User getUser(String email);
	String getToken (User user);
	boolean changePassword(String uuid, String password) ;
	boolean sendConformationMail(String email);
	
}
