package com.bridgeIt.user.service;


import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.rowset.serial.SerialException;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.bridgeIt.user.BaseResponse;
import com.bridgeIt.user.dao.UserDao;
import com.bridgeIt.user.model.TradeUser;
import com.bridgeIt.user.model.User;
import com.bridgeIt.user.model.UserAccount;
import com.bridgeIt.user.service.utility.JwtToken;
import com.bridgeIt.user.service.utility.RabbitMsgSender;
import com.bridgeIt.user.service.utility.TradeUtil;
import com.bridgeIt.user.service.utility.UserMail;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserServiceImp implements UserService {

	
	@Autowired
	UserDao dao;
	
	@Autowired
	BaseResponse response;
	
	
	@Autowired
	JwtToken token;
	
	
	@Autowired
	RabbitMsgSender sender;
	
	@Autowired
	UserMail mail;
	
	@Autowired
	HFCAClient caClient;
	
	@Autowired
	HFClient client;
	
	@Autowired
	TradeUtil tradeUtil;
	
	@Autowired
	Channel channel;
	
	
	@Override
	public BaseResponse userReg(User user) {
	
		boolean outcome=dao.presence(user);
		if(outcome!=true) {


				System.out.println("after presence");
				String uuid=UUID.randomUUID().toString();
				user.setAuthenticatedUserKey(uuid);
				user.setVerified(true);
				
				try {
					dao.insertBeforeAcc(user);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				User user1 = dao.getUserByEmail(user.getEmail());
				int accountInt = user1.getId();
				String accountStr = Integer.toString(accountInt);
				TradeUser admin = tradeUtil.getAdmin(caClient);
				user1.setAccounNumber(accountStr);
				//convertUserAccountToByteArray(user.)
				TradeUser userAcc = tradeUtil.makeTradeAccount(caClient, admin, user1.getAccounNumber(), user1.getRole());
				byte [] userAccountByte =tradeUtil.convertUserAccountToByteArray(userAcc);
				user1.setUserAccount(userAccountByte);
				
				try {
					dao.updateUserAccountAndAccountNo(user1.getAccounNumber(), user1.getUserAccount(), user1.getEmail());
				} catch (SerialException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Channel channel = tradeUtil.getChannel(client, userAcc);
				System.out.println(channel.getName()+" is channel name");
				  int bal= user.getBalance();
			        String balance =Integer.toString(bal);
				String [] args = new String[] {user1.getAccounNumber(),user1.getRole(),balance,user1.getBank()};
				try {
					tradeUtil.transactionInvokeBlockChain(client, "createAccount", args,channel);
				} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
				
					e.printStackTrace();
				}
			//	channel.shutdown(true);
				response.setCode(200);
				response.setStatus(HttpStatus.OK);
				response.setMessage("you are registered successfully...");
				response.setErrors(null);
				
				
//				String url="http://localhost:8080/user/verification/getVerified/";
//				String verificationUrl=url+uuid;
//				mail.setFrom("tradefinancebridgelabz@gmail.com");
//				mail.setTo(user.getEmail());
//				mail.setMessage(verificationUrl);
//				sender.sendMsg(mail);
//				
//				response.setCode(200);
//				response.setStatus(HttpStatus.OK);
//				response.setMessage("please check your email to get verified..");
//				response.setErrors(null);
				return response;
				

		}
		response.setCode(400);
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setMessage("User already exist");
		response.setErrors(null);
		
		
		return response;
	}

	@Override
	public boolean login(String email, String password) {

		boolean outcome=dao.checkUser(email, password);
		if (outcome) {
			System.out.println(true +" check user");
			return true;
		}
		System.out.println(false +" check user");
		return false;
	}

	@Override
	public boolean verify(String uniqueId) {
		
		boolean result = dao.getVerified(uniqueId);
		
		
		return result;
		
	}

	@Override
	public User getUser(String email) {

		User user=dao.fetchUserByEmail(email);
		return user;
	}

	@Override
	public String getToken(User user) {
		
//		int userId=user.getId();
//		String id = Integer.toString(userId);
		String jwtToken =token.createJwt( user.getEmail(),user.getRole(), 720000);
		
		return jwtToken;
	}

	@Override
	public boolean changePassword(String uuid, String password) {
		
		String newPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		boolean result = dao.resetPassword(uuid, newPassword);
		
		return result;
	}
	
	
	@Override
	public boolean sendConformationMail(String email) {
		
	//	String uuid = dao.getUUid(email);
		User user	= dao.fetchUserByEmail(email);
		if (user==null) {
			
			return false;
		}
		dao.insertForgotPassword(user);
		mail.setFrom("tradefinancebridgelabz@gmail.com");
		mail.setTo(email);
		String url="http://127.0.0.1:3000/#!/reset_password/";
		String verificationUrl=url+user.getAuthenticatedUserKey();
		mail.setMessage(verificationUrl);
		sender.sendMsg(mail);
		
		
		return true;
	}

	@Override
	public boolean checkSessionPassword(String uuid) {
		int outcome = dao.checkSession(uuid);
		dao.removeTempUser(uuid);
		if(outcome==1) {
			
			return true;
		}
		if(outcome==-1) {
			return false;
		}
		
		return false;
	}
	
	@Override
	public int getUserBalance(String accountNumber) {
		
		boolean result = dao.uniqueAccountNumber(accountNumber);

		if (!result) {
		//	TradeUser admin = tradeUtil.getAdmin(caClient);
	//		byte [] tradeUserByte =dao.getUserTradeAccount(accountNumber);
		//	TradeUser tradeUser = tradeUtil.convertByteArrayToObject(tradeUserByte);
		//	System.out.println(tradeUser);
			 
		//	tradeUtil.getChannel(client, tradeUser);
			 
			//tradeUtil.queryBlockChain(client, function, args);
			 String [] args = new String[] {accountNumber};
			// ObjectMapper mapper = new ObjectMapper();
		//	 mapper.
			try {
				List<String> responses = tradeUtil.queryBlockChain(client, "getBalance", args,channel);
				String response = responses.get(0);
				System.out.println(response +" is response for query");
			
				int balance = Integer.parseInt(response);

				dao.updateBalance(accountNumber, balance);
				return balance ;
			
			} catch (ProposalException e) {
			
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				
				e.printStackTrace();
			}
		}
		return -1;
			
	}
	
	

}
