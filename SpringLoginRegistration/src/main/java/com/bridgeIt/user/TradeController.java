package com.bridgeIt.user;

import java.util.List;

import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgeIt.user.dao.UserDao;
import com.bridgeIt.user.model.Contract;
import com.bridgeIt.user.model.ContractLists;
import com.bridgeIt.user.service.TradeService;
import com.bridgeIt.user.service.utility.TradeUtil;

@RestController
public class TradeController {

	
	@Autowired
	TradeService tradeService;
	
	@Autowired
	UserDao dao;
	
	@Autowired
	Channel channel;
	
	@Autowired
	TradeUtil tradeUtil;
	
	@RequestMapping(value="/createContract" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ContractResponse> createContract(@RequestBody Contract contract){
		ContractResponse response = new ContractResponse();
		boolean saved = tradeService.insertContract(contract);
	
		if (saved) {
			response.setCode(200);
			response.setStatus(HttpStatus.OK);
			response.setContract(contract);
			return new ResponseEntity<ContractResponse>(response,HttpStatus.OK);
		}
		response.setCode(400);
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setContract(null);
		return new ResponseEntity<ContractResponse>(response,HttpStatus.BAD_REQUEST);
		
		
	}
	
	@RequestMapping(value="/updateContract" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ContractResponse> updateContract(@RequestBody Contract contract,@RequestHeader("token") String jwtToken){
		
		System.out.println(jwtToken);
		System.out.println(contract);
		ContractResponse response = new ContractResponse();
		boolean saved = tradeService.updateContract(jwtToken, contract);
	
		if (saved) {
			response.setCode(200);
			response.setStatus(HttpStatus.OK);
			Contract contractResponse =tradeService.getContractResponse(contract.getContractId());
			response.setContract(contractResponse);
			return new ResponseEntity<ContractResponse>(response,HttpStatus.OK);
		}
		response.setCode(400);
		response.setStatus(HttpStatus.BAD_REQUEST);
		response.setContract(null);
		return new ResponseEntity<ContractResponse>(response,HttpStatus.BAD_REQUEST);
		
		
	}
	
	@RequestMapping(value="/getAllContract" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ContractLists> getContract(@RequestHeader("token") String jwtToken){
		
	 List<Contract> usersContracts = tradeService.getAllContract(jwtToken);
	 ContractLists contractsResponse = new ContractLists();
	 
	 if(usersContracts.isEmpty()||usersContracts==null) {
		 contractsResponse.setCode(400);
		 contractsResponse.setContracts(null);
		 contractsResponse.setStatus(HttpStatus.BAD_REQUEST);
		 return new ResponseEntity<ContractLists>(contractsResponse ,HttpStatus.BAD_REQUEST);
	 }
	 
	 
	 contractsResponse.setCode(200);
	 contractsResponse.setContracts(usersContracts);
	 contractsResponse.setStatus(HttpStatus.OK);
	 return new ResponseEntity<ContractLists>(contractsResponse ,HttpStatus.BAD_REQUEST);
		
	}
	
	
	
}
