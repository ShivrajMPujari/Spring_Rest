package com.bridgeIt.user.service;


import java.util.List;

import com.bridgeIt.user.model.Contract;

public interface TradeService {

	public boolean insertContract (Contract contract,String jwtToken);
	boolean updateContract(String jwtToken, Contract contract);
	Contract getContractResponse (String contractId);
	public List<Contract>  getAllContract(String jwt);
	public Contract getContractFromBlockChain ( String contractId ,String jwtToken );
}
