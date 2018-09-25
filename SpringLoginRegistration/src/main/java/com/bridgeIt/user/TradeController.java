package com.bridgeIt.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import org.hyperledger.fabric.sdk.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgeIt.user.dao.UserDao;
import com.bridgeIt.user.model.Contract;
import com.bridgeIt.user.model.ContractId;
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
	public ResponseEntity<ContractResponse> createContract(@RequestBody Contract contract,@RequestHeader("token") String jwtToken){
		ContractResponse response = new ContractResponse();
		
		boolean saved = tradeService.insertContract(contract,jwtToken);
	
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
	public ResponseEntity<ContractLists> getAllContracts(@RequestHeader("token") String jwtToken){
		
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
	 return new ResponseEntity<ContractLists>(contractsResponse ,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/getContract" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ContractResponse> getContract(@RequestBody ContractId contractId ,@RequestHeader("token") String jwtToken){
		
		Contract contract = tradeService.getContractFromBlockChain(contractId.getContractId(), jwtToken);
		ContractResponse response = new ContractResponse();
		if(contract==null) {
			response.setCode(400);
			response.setStatus(HttpStatus.BAD_REQUEST);
			response.setContract(null);
			return new ResponseEntity<ContractResponse>(response,HttpStatus.BAD_REQUEST);
		}
		response.setContract(contract);
		response.setCode(200);
		response.setStatus(HttpStatus.OK);
		
		return new ResponseEntity<ContractResponse>(response,HttpStatus.OK);	
	}
	
	
	@RequestMapping(value="/download/{contractId}/{fileName}" , method = RequestMethod.GET)
	public ResponseEntity<String> download(@PathVariable("contractId") String contractId ,@PathVariable("fileName") String filename,@RequestHeader("token") String jwtToken ){
		
		System.out.println(contractId);
		System.out.println(jwtToken);
		Path downloadPath =	Paths.get("//home//bridgelabz//Documents//contracts//"+contractId+"//"+filename+".jpg");
		byte[] fileByte = null;
//		ByteArrayResource resource = null;
		try {
			fileByte = Files.readAllBytes(downloadPath);
		
//		 resource = new ByteArrayResource(fileByte);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		String fileBase64	= Base64.getEncoder().encodeToString(fileByte);
//		//http://localhost:8080/user/download/017/billOfLading.jpg
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION,
//		                  "attachment;filename=" + downloadPath.getFileName().toString())
//				.contentType(MediaType.IMAGE_JPEG).contentLength(fileByte.length)
//				.body(resource);
				
		return new ResponseEntity<String>(fileBase64,HttpStatus.OK);
	
	}
	
	@RequestMapping(value="/consensus" , method = RequestMethod.POST, produces="application/json")
	public ResponseEntity<ContractResponse> consensus(@RequestHeader("token") String jwtToken,@RequestBody Contract contract){
		
		
		ContractResponse contractResponse = tradeService.exporterConsensus(jwtToken, contract);
		
		
		
		return new ResponseEntity<ContractResponse>(contractResponse, contractResponse.getStatus());
	}
	
	
	
	
	
	
}
