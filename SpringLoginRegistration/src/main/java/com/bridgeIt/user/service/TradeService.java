package com.bridgeIt.user.service;

import org.hyperledger.fabric_ca.sdk.HFCAClient;

import com.bridgeIt.user.model.TradeUser;

public interface TradeService {

	TradeUser getAdmin(HFCAClient caClient);
	TradeUser tryDeserializeAdmin(String name);
	TradeUser deSerializeAdmin(String name);
	void serializeAdmin(TradeUser user);
	
	
}
