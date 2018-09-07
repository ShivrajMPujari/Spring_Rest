package com.bridgeIt.user.service.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bridgeIt.user.model.TradeUser;
import com.bridgeIt.user.model.User;

@Component
public class TradeUtil {


	public TradeUser getAdmin(HFCAClient caClient) {
		System.out.println("try deserialize admin");
        TradeUser admin = tryDeserializeAdmin("admin");
        System.out.println(admin+" got admin while trydeserializing...");
       // TradeUser admin =null;
        if (admin == null) {
        	System.out.println(" admin is null");
            Enrollment adminEnrollment = null;
			try {
				adminEnrollment = caClient.enroll("admin", "adminpw");
			} catch (EnrollmentException | InvalidArgumentException e) {
				e.printStackTrace();
			}
            admin = new TradeUser("admin", "importer", "ImporterMSP", adminEnrollment);
            serializeAdmin(admin);
            System.out.println("admin serialized...");
        }
        
        return admin;
    }

	public static TradeUser tryDeserializeAdmin(String name) {
	//	Path path = Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso");	
		//System.out.println(path.toString()+" is the path "+path.getRoot());
        if (Files.exists(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso"))) {
        	System.out.println();
            return deSerializeAdmin(name);
        }
        return null;
    }


	public static TradeUser deSerializeAdmin(String name) {
        try (ObjectInputStream decoder = new ObjectInputStream(
                Files.newInputStream(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso")))) {
            return (TradeUser) decoder.readObject();
        } catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
    }

	public static void serializeAdmin(TradeUser tradeUser) {
		System.out.println(tradeUser+" is serializing...");
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
                Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+tradeUser.getName() + ".jso")))) {
            oos.writeObject(tradeUser);
        	System.out.println(tradeUser+" is written...");
        } catch (Exception e) {

			e.printStackTrace();
		}
		
	}

	public byte[] convertUserAccountToByteArray(TradeUser user) {
		
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			e.printStackTrace();
		}
	      try {
			oos.writeObject(user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	      try {
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      byte [] data = bos.toByteArray();
		
		return data;
	}

	public Channel getChannel(HFClient client,TradeUser admin) {
		
	  
	  try {
		client.setUserContext(admin);
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}
	//	client.setUserContext(userContext)
		
	    Peer peer1 = null;
		try {
			peer1 = client.newPeer("peer0.importer.bridgeIt.com", "grpc://localhost:7051");
		} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


      EventHub eventHub1 = null;
	try {
		eventHub1 = client.newEventHub("eventhub01", "grpc://localhost:7053");
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}

      Orderer orderer = null;
	try {
		orderer = client.newOrderer("orderer.bridgeIt.com", "grpc://localhost:7050");
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}

      Channel channel = null;
	try {
		channel = client.newChannel("mychannel");
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}
      try {
		channel.addPeer(peer1);
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}

      try {
		channel.addEventHub(eventHub1);
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}

      try {
		channel.addOrderer(orderer);
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {

		e.printStackTrace();
	}
      try {
		channel.initialize();
	} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException | TransactionException e) {

		e.printStackTrace();
	}
      return channel;
	}

	public TradeUser convertByteArrayToObject(byte[] data) {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(in);
		} catch (IOException e) {
		
			e.printStackTrace();
		}
	    try {
			return (TradeUser) is.readObject();
		} catch (ClassNotFoundException | IOException e) {

			e.printStackTrace();
		}
		return null;

	}
	
	
	public TradeUser makeTradeAccount (HFCAClient caClient,TradeUser registrar,String userId,String role) {
		 RegistrationRequest rr = null;
		try {
			rr = new RegistrationRequest(userId, "org1");
		} catch (Exception e) {
			e.printStackTrace();
		}
         String enrollmentSecret = null;
		try {
			enrollmentSecret = caClient.register(rr, registrar);
		} catch (RegistrationException | InvalidArgumentException e) {

			e.printStackTrace();
		}
         Enrollment enrollment = null;
		try {
			enrollment = caClient.enroll(userId, enrollmentSecret);
		} catch (EnrollmentException | InvalidArgumentException e) {

			e.printStackTrace();
		}
		TradeUser tradeUser =null;
		switch (role) {
		case "importer":{
			
		tradeUser = new TradeUser(userId, "importer", "ImporterMSP", enrollment);
			 break;
			
			}
		case "exporter":{
			tradeUser = new TradeUser(userId, "exporter", "ExporterMSP", enrollment);
			break;
		}
		case "custom":{
			tradeUser = new TradeUser(userId, "custom", "CustomMSP", enrollment);
			break;
			
		}
		case "importerBank":{
			tradeUser = new TradeUser(userId, "importerBank", "ImporterBankMSP", enrollment);
			break;
			
		}
		case "insurance":{
			tradeUser = new TradeUser(userId, "insurance", "InsuranceMSP", enrollment);
			break;
			
		}

		default:
			break;
		}
			
		return tradeUser; 
		
	}
	
	
	public boolean transactionInvokeBlockChain(HFClient client,String chaincodeFunction,String[] args,Channel channel) throws org.hyperledger.fabric.sdk.exception.InvalidArgumentException {
	    	
	    	
	      //  Channel channel = client.getChannel("mychannel");
	        TransactionProposalRequest	tqr = client.newTransactionProposalRequest();
	        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
	        tqr.setChaincodeID(tradeFinanceCCId);
	        tqr.setFcn(chaincodeFunction);
	        tqr.setArgs(args);
	        Collection<ProposalResponse> responses = null ;
	        try {
	        	 responses = channel.sendTransactionProposal(tqr);
	        	List<ProposalResponse> invalid = responses.stream().filter(res -> res.isInvalid()).collect(Collectors.toList());
	        	if (!invalid.isEmpty()) {
	        		
	        		invalid.forEach(response -> {
	        			System.out.println(response.getMessage());
	        		});
	        		
	        	}
	        } catch (ProposalException e) {
				e.printStackTrace();
				return false;
			}
	         
	        try {
	        	 BlockEvent.TransactionEvent event = channel.sendTransaction(responses).get(60,TimeUnit.SECONDS);
	        	 if (event.isValid()) {
	        		 System.out.println(event.getTransactionID()+" transaction is valid ");
	        		 return true;
	        	 }else {
	        		 System.out.println(event.getTransactionID() + " transaction is invalid");
	        		 return false;
	        	 }
	        } catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
				return false;
			}
			
	        
	    //	channel.shutdown(true);
	    }
		
	 public List<String> queryBlockChain(HFClient client,String function,String [] args,Channel channel) throws ProposalException, InvalidArgumentException {
	        // get channel instance from client
	       // Channel channel = client.getChannel("mychannel");
	        // create chaincode request
	        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
	        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
	        qpr.setChaincodeID(tradeFinanceCCId);
	        // CC function to be called
	        qpr.setFcn(function);
	        qpr.setArgs(args);
	    	List<String> list = new ArrayList<String>();
	        Collection<ProposalResponse> res = null;
			try {
				res = channel.queryByChaincode(qpr);
				
			} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
				e.printStackTrace();
			}
		
	        // display response
	        for (ProposalResponse pres : res) {
	            String stringResponse = null;
				try {
					stringResponse = new String(pres.getChaincodeActionResponsePayload());
				} catch (org.hyperledger.fabric.sdk.exception.InvalidArgumentException e) {
					e.printStackTrace();
				}
	           // log.info(stringResponse);
	            System.out.println(stringResponse);
	            list.add(stringResponse);
	        }
	        
	      //  channel.getChannelConfigurationBytes();
	     //   channel.shutdown(true);
			return list;
	    }

	 
	 
}
