package com.bridgeIt.user.service;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.bridgeIt.user.dao.UserDao;
import com.bridgeIt.user.model.Contract;
import com.bridgeIt.user.model.TradeUser;
import com.bridgeIt.user.service.utility.TradeUtil;

public class TradeServiceImp implements TradeService {
		
	@Autowired
	UserDao dao;
	
	
	@Autowired
	HFClient client;
	
	@Autowired
	HFCAClient caClient;
	
	@Autowired
	TradeUtil tradeUtil;
	
	public boolean insertContract (Contract contract) {
		
		boolean insertion = dao.saveContract(contract);
		
		if(insertion) {
			TradeUser admin = tradeUtil.getAdmin(caClient);
			
			
		}
		
		return false;
	}
	
	
	
}
