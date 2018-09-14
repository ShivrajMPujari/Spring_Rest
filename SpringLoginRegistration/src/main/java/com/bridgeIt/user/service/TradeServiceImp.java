package com.bridgeIt.user.service;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bridgeIt.user.dao.UserDao;
import com.bridgeIt.user.model.Contract;
import com.bridgeIt.user.model.User;
import com.bridgeIt.user.service.utility.JwtToken;
import com.bridgeIt.user.service.utility.TradeUtil;

@Component
public class TradeServiceImp implements TradeService {
		
	@Autowired
	UserDao dao;
	
	
	@Autowired
	HFClient client;
	
	@Autowired
	HFCAClient caClient;
	
	@Autowired
	TradeUtil tradeUtil;
	
	@Autowired
	Channel channel;
	
	@Autowired
	JwtToken token;
	
	@Autowired
	UserService userService;
	
	public boolean insertContract (Contract contract,String jwtToken) {
		
		String email = token.getJwtId(jwtToken);
		
		User user = dao.getUserByEmail(email);
		System.out.println(user);
		if (user == null) {
			return false;
		}
		
		contract.setExporterCheck(true);
		contract.setCompletion(false);
		contract.setPointer(contract.getCustomId());
		boolean insertion = dao.saveContract(contract);
		
		if(insertion) {
			//TradeUser admin = tradeUtil.getAdmin(caClient);
			String value =  Integer.toString(contract.getValue());
		//	var contract = Contract{ContractID: args[0], ContentDescription: args[1], Value: contractValue, ExporterID: args[3], CustomID: args[4], InsuranceID: args[5], ImporterID: args[6], ImporterBankID: args[7], PortOfLoading: args[8], PortOfEntry: args[9], ImporterCheck: false, ExporterCheck: true, CustomCheck: false, ImporterBankCheck: false, InsuranceCheck: false}

			String [] args = {contract.getContractId(),contract.getContractDescription(),value,contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry()};
			try {
				
				tradeUtil.transactionInvokeBlockChain(client, "createContract", args, channel);
				
				return true;
			} catch (InvalidArgumentException e) {
			
				e.printStackTrace();
				return false;
			}
			
		}
		return false;
		
		
	}
	
	public boolean updateContractInBlockChain(String jwtToken, Contract contract) {
		
		String email = null;
		try {
			System.out.println("token is "+jwtToken);
			email = token.getJwtId(jwtToken);
			System.out.println(email);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		User user = dao.getUserByEmail(email);
		System.out.println(user);
		
		String [] args = {user.getAccountNumber(),contract.getContractId()};
		System.out.println(user.getAccountNumber()+"---- "+contract.getContractId());
		switch (user.getRole()) {
		
		case "custom":{
			
			try {
				System.out.println("calling custom assurity");
				tradeUtil.transactionInvokeBlockChain(client, "customAssurity", args, channel);
				return true;
			} catch (InvalidArgumentException e) {
			
				e.printStackTrace();
				return false;
			}
			
		}
		case "insurance":{
			System.out.println("calling insurance assurity");
				try {
					tradeUtil.transactionInvokeBlockChain(client, "insuranceAssurity", args, channel);
					return true;
				} catch (Exception e) {
				
					e.printStackTrace();
					return false;
				}
				
			}
		
		case "importer":{
			System.out.println("calling importer assurity");

			try {
				tradeUtil.transactionInvokeBlockChain(client, "importerAssurity", args, channel);
				return true;
			} catch (InvalidArgumentException e) {
				
				e.printStackTrace();
				return false;
			}
			
		}
		case "importerBank":{
			System.out.println("calling importerBank assurity");

			try {
				System.out.println("calling importerBank assurity");
				tradeUtil.transactionInvokeBlockChain(client, "importerBankAssurity", args, channel);
				updateBalanceByAcccountNo(contract.getExporterId());
				updateBalanceByAcccountNo(contract.getImporterId());
				boolean result = dao.completionOfContract(contract.getContractId());
				if(result) {
					return true;
				}else {
					return false;
				}
				
			} catch (Exception e) {
			
				e.printStackTrace();
				return false;
			}
			
		}
		
		
		}
		
	//	boolean updated = dao.updateContract(contract);
		
//		if(updated) {
//			
//			String value =  Integer.toString(contract.getValue());
//			String [] args = {contract.getContractId(),contract.getContractDescription(),value,contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry()};
//			try {
//				tradeUtil.transactionInvokeBlockChain(client, "createContract", args, channel);
//				return true;
//			} catch (InvalidArgumentException e) {
//			
//				e.printStackTrace();
//				return false;
//			}
//			
//		}
		
		
		
		
		return false;
	}
	
	public boolean updateContractInDB(String contractId) {
		
		String [] args = {contractId};
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<String>  responses = tradeUtil.queryBlockChain(client, "getContract", args, channel);
			String response = responses.get(0);
			try {
				
			Contract contract =	mapper.readValue(response, Contract.class);
			
			System.out.println(contract);
			
			boolean contractUpdated = dao.updateContract(contract);
			
			if(contractUpdated) {
				
				return true;
			}else {
				return false;
			}
			
			} catch (JsonParseException e) {
	
				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (ProposalException e) {
			e.printStackTrace();
		} catch (org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	
	public boolean updateContract(String jwtToken, Contract contract) {
		
		
		boolean updatedInBc = updateContractInBlockChain(jwtToken, contract);
		
		if (updatedInBc) {
			
			boolean updatedInDb =  updateContractInDB(contract.getContractId());
			
			if (updatedInDb) {
				return true;
			}else {
				dao.completionOfContract(contract.getContractId());
				return false;
			}
		}else {
			
			dao.completionOfContract(contract.getContractId());
			return false;
		}
		
		
	}
	
	public List<Contract>  getAllContract(String jwt) {
		
		String tokenId = token.getJwtId(jwt);
		User user = dao.fetchUserByEmail(tokenId);

		List<Contract> allContract = dao.gellAllContract(user.getAccountNumber(),user.getRole());
		
		return allContract;
	}
	
	public Contract getContractResponse (String contractId) {
		
		Contract contract =	dao.getContract(contractId);
		
		return contract;
	}
	
	
	public Contract getContractFromBlockChain ( String contractId ,String jwtToken ){
		
		String [] args = {contractId};
		ObjectMapper mapper = new ObjectMapper();
		Contract contract = null;
		try {
			List<String>  responses = tradeUtil.queryBlockChain(client, "getContract", args, channel);
			String response = responses.get(0);
			try {
				
				contract = mapper.readValue(response, Contract.class);
			
			} catch (JsonParseException e) {
	
				e.printStackTrace();
			} catch (JsonMappingException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (ProposalException e) {
			e.printStackTrace();
		} catch (org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
			e.printStackTrace();
		}
		
		
		return contract;
	}
	
	public void updateBalanceByAcccountNo(String accountNumber) {
		
		boolean result = dao.uniqueAccountNumber(accountNumber);

		if (!result) {

			 String [] args = new String[] {accountNumber};

			try {
				List<String> responses = tradeUtil.queryBlockChain(client, "getBalance", args,channel);
				String response = responses.get(0);
				System.out.println(response +" is response for query");
			
				int balance = Integer.parseInt(response);

				dao.updateBalance(accountNumber, balance);
				
			
			} catch (ProposalException e) {
			
				e.printStackTrace();
			} catch (org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	
}
