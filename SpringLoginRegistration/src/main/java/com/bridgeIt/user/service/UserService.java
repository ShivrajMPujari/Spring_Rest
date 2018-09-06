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
	boolean checkSessionPassword(String uuid);
	int getUserBalance(String accountNumber);
	
//	TradeUser getAdmin();
//	TradeUser tryDeserializeAdmin(String name);
//	TradeUser deSerializeAdmin(String name);
//	void serializeAdmin(TradeUser user);
//	Channel getChannel(HFClient client);
//	
//	byte [] convertUserAccountToByteArray(TradeUser user);
//	TradeUser convertByteArrayToObject(byte [] data);
//	
//	TradeUser makeTradeAccount(HFCAClient caClient,TradeUser registrar,String userId,String role);
	
	
}
