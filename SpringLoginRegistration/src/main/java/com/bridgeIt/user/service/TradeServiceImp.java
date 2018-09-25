package com.bridgeIt.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.bridgeIt.user.ContractResponse;
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
		
		byte[] imgBillOfLading = Base64.getDecoder().decode(contract.getBillOfLading());
		byte[] imgLetterOfCredit = Base64.getDecoder().decode(contract.getLetterOfCredit());
		//file upload 
		Path path = Paths.get("//home//bridgelabz//Documents//contracts//"+contract.getContractId());
		
		try {
			Files.createDirectory(path);
			Path path1 =Paths.get("//home//bridgelabz//Documents//contracts//"+contract.getContractId()+"//billOfLading.jpg");
			Files.write(path1, imgBillOfLading);
			Path path2 =Paths.get("//home//bridgelabz//Documents//contracts//"+contract.getContractId()+"//letterOfCredit.jpg");
			Files.write(path2, imgLetterOfCredit);
			String urlBillOfLading = "http://localhost:8081/user/download/"+contract.getContractId()+"/billOfLading.jpg";
			contract.setBillOfLading(urlBillOfLading);	
			String urlLetterOfCredit = "http://localhost:8081/user/download/"+contract.getContractId()+"/letterOfCredit.jpg";;
			contract.setLetterOfCredit(urlLetterOfCredit);
		} catch (IOException e2) {
			e2.printStackTrace();
			return false;
		}
		
		
		
		boolean insertion = false;
		try {
			insertion = dao.saveContract(contract);
		} catch (SerialException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if(insertion) {
			//TradeUser admin = tradeUtil.getAdmin(caClient);
			String value =  Integer.toString(contract.getValue());
		//	var contract = Contract{ContractID: args[0], ContentDescription: args[1], Value: contractValue, ExporterID: args[3], CustomID: args[4], InsuranceID: args[5], ImporterID: args[6], ImporterBankID: args[7], PortOfLoading: args[8], PortOfEntry: args[9], ImporterCheck: false, ExporterCheck: true, CustomCheck: false, ImporterBankCheck: false, InsuranceCheck: false}
			
			String [] args = {contract.getContractId(),contract.getContractDescription(),value,contract.getExporterId(),contract.getCustomId(),contract.getInsuranceId(),contract.getImporterId(),contract.getImporterBankId(),contract.getPortOfLoading(),contract.getPortOfEntry(),contract.getLetterOfCredit(),contract.getBillOfLading()};
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
				boolean result = tradeUtil.transactionInvokeBlockChain(client, "customAssurity", args, channel);
				
				if(!result) {
					dao.completionOfContract(contract.getContractId());
					return false;
				}
				
				return true;
			} catch (InvalidArgumentException e) {
			
				e.printStackTrace();
				return false;
			}
			
		}
		case "insurance":{
			System.out.println("calling insurance assurity");
				try {
					boolean result = 	tradeUtil.transactionInvokeBlockChain(client, "insuranceAssurity", args, channel);
					if(!result) {
						dao.completionOfContract(contract.getContractId());
						return false;
					}
					return true;
				} catch (Exception e) {
				
					e.printStackTrace();
					return false;
				}
				
			}
		
		case "importer":{
			System.out.println("calling importer assurity");

			try {
				boolean result = 	tradeUtil.transactionInvokeBlockChain(client, "importerAssurity", args, channel);
				if(!result) {
					dao.completionOfContract(contract.getContractId());
					return false;
				}
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
				boolean result = tradeUtil.transactionInvokeBlockChain(client, "importerBankAssurity", args, channel);
				if(!result) {
					dao.completionOfContract(contract.getContractId());
					return false;
				}
				
				updateBalanceByAcccountNo(contract.getExporterId());
				updateBalanceByAcccountNo(contract.getImporterId());
				boolean result1 = dao.completionOfContract(contract.getContractId());
				if(result1) {
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
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	
	
	
	public ContractResponse exporterConsensus(String jwtToken, Contract contract) {
		ContractResponse contractResponse = new ContractResponse();
		boolean isUnique = dao.uniqueContract(contract.getContractId());
		
		if (isUnique) {
			
			boolean contractCreated = insertContract(contract, jwtToken);
			
			if(contractCreated) {
				
			performConsensus(contract);
				
			boolean updatedContract = updateContractInDB(contract.getContractId());
				if(updatedContract == false) {
					
					contractResponse.setCode(500);
					contractResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
					contractResponse.setMessage("Contract upadtion failed");
					contractResponse.setContract(null);
					return contractResponse;
					
				}
				
			dao.completionOfContract(contract.getContractId());
				
			}
			
			
			
			
		}else {
			
			contractResponse.setCode(400);
			contractResponse.setStatus(HttpStatus.BAD_REQUEST);
			contractResponse.setMessage("Contract already exist");
			contractResponse.setContract(null);
			return contractResponse;
		}
		
		
		contractResponse.setCode(200);
		contractResponse.setStatus(HttpStatus.OK);
		contractResponse.setMessage("success");
		Contract updatedContract = dao.getContract(contract.getContractId());
		contractResponse.setContract(updatedContract);
		return contractResponse;
	} 
	
	
	public boolean performConsensus(Contract contract) {
		
		String [] customArgs = {contract.getCustomId(),contract.getContractId()};
		String [] insuranceArgs = {contract.getInsuranceId(),contract.getContractId()};
		String [] importerArgs = {contract.getImporterId(),contract.getContractId()};
		String [] importerBankArgs = {contract.getImporterBankId(),contract.getContractId()};
		
		try {
			boolean customTransaction = tradeUtil.transactionInvokeBlockChain(client, "customAssurity", customArgs, channel);
			if (customTransaction == false) {
				dao.completionOfContract(contract.getCustomId());
				return false;
			}
			System.out.println("Transaction Accepted  by Custom");
		} catch (InvalidArgumentException e) {
			
			e.printStackTrace();
			return false;
		}
		
		try {
			boolean insuranceTransaction =	tradeUtil.transactionInvokeBlockChain(client, "insuranceAssurity", insuranceArgs, channel);
			if(insuranceTransaction == false) {	
				dao.completionOfContract(contract.getCustomId());
				return false;
			}
			System.out.println("Transaction Accepted  by Insurance");
		} catch (InvalidArgumentException e) {
		
			e.printStackTrace();
			return false;
		}
		
		try {
			boolean importerTransaction = tradeUtil.transactionInvokeBlockChain(client, "importerAssurity", importerArgs, channel);
			if(importerTransaction==false) {
				dao.completionOfContract(contract.getCustomId());
				return false;
			}
			System.out.println("Transaction Accepted  by Importer");
		} catch (InvalidArgumentException e) {
		
			e.printStackTrace();
			return false;
		}
		
		try {
			boolean importerBankTransaction = tradeUtil.transactionInvokeBlockChain(client, "importerBankAssurity", importerBankArgs, channel);
			if (importerBankTransaction == false) {
				dao.completionOfContract(contract.getCustomId());
				return false;
			}
			System.out.println("Transaction Accepted by ImporterBank");
		} catch (InvalidArgumentException e) {
			e.printStackTrace();
			return false;
		}
		
		
		dao.completionOfContract(contract.getCustomId());
		return true;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
