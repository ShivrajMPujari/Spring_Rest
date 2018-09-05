package com.bridgeIt.user.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
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
import com.bridgeIt.user.service.utility.JwtToken;
import com.bridgeIt.user.service.utility.RabbitMsgSender;
import com.bridgeIt.user.service.utility.TradeUtil;
import com.bridgeIt.user.service.utility.UserMail;

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
	
	@Override
	public BaseResponse userReg(User user) {
	
		boolean outcome=dao.presence(user);
		if(outcome!=true) {
			
			boolean isUniqueAccount = dao.uniqueAccountNumber(user.getAccounNumber());
			
			if (isUniqueAccount) {

				System.out.println("after presence");
				String uuid=UUID.randomUUID().toString();
				user.setAuthenticatedUserKey(uuid);
				user.setVerified(true);
				TradeUser admin = tradeUtil.getAdmin(caClient);
				//convertUserAccountToByteArray(user.)
				TradeUser userAcc = tradeUtil.makeTradeAccount(caClient, admin, user.getAccounNumber(), user.getRole());
				byte [] userAccountByte =tradeUtil.convertUserAccountToByteArray(userAcc);
				user.setUserAccount(userAccountByte);
				
				try {
					dao.insert(user);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				 Channel channel = tradeUtil.getChannel(client, admin);
				System.out.println(channel.getName()+" is channel name");
				try {
					tradeUtil.transactionInvokeBlockChain(client, "createAccount", user);
				} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
				
					e.printStackTrace();
				}
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
			else {
				response.setCode(400);
				response.setStatus(HttpStatus.BAD_REQUEST);
				response.setMessage("account number must be unique exist");
				response.setErrors(null);	
				
			}

			
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
			return true;
		}
		
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
		
		int userId=user.getId();
		String id = Integer.toString(userId);
		String jwtToken =token.createJwt(id, user.getEmail(), 720000);
		
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
	
	
	
	
	
	
}
