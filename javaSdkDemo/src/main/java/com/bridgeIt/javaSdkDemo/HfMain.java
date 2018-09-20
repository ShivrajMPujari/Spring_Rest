package com.bridgeIt.javaSdkDemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;
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
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HfMain {
	
    private static final Logger log = Logger.getLogger(HfMain.class);
    
    public static void main(String[] args) throws Exception {
    	String name = "admin";
		Path path = Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso");	
		 if (Files.exists(path)) {
	        	System.out.println(path);
	           
	        }
    	System.out.println(path);
    	System.out.println(false);
    	
        // create fabric-ca client
        HFCAClient caClient = getHFCaClient("http://localhost:7054");

        // enroll or load admin
        AppUser admin = getAdmin(caClient);
       // log.info(admin);
        System.out.println(admin);
        // register and enroll new user
        AppUser appUser = getUser(caClient, admin, "hfuserbfghfx");
     //   log.info(appUser);
        System.out.println(appUser);
        // get HFC client instance
        HFClient client = getHfClient();
        // set user context
        client.setUserContext(admin);     
        // get HFC channel using the client
        Channel channel = getChannel(client);
       // log.info("Channel: " + channel.getName());
        System.out.println("Channel: " + channel.getName());
        System.out.println("querying.....");
        // call query blockchain example
        //queryBlockChain(client);
        transactionInvokeBlockChain(client);
    }
    
    static void transactionInvokeBlockChain(HFClient client) throws JsonProcessingException, InvalidArgumentException {
    	
    	
        Channel channel = client.getChannel("mychannel");
        TransactionProposalRequest	tqr = client.newTransactionProposalRequest();
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        tqr.setChaincodeID(tradeFinanceCCId);
        tqr.setFcn("createAccount");
        tqr.setArgs(new String[] {"102","custom","20000","SBI BANK"});
        ChaincodeEventListener chaincodeEventListener = new ChaincodeEvent();
        //System.out.println("________________________________________________--"
        		//+ "\n___________________"+new ObjectMapper().writeValueAsString(tradeFinanceCCId));
        Pattern pattern = Pattern.compile(".*");
        Pattern pattern2 = Pattern.compile(".*");
        channel.registerChaincodeEventListener(pattern, pattern2, chaincodeEventListener);
       
        Collection<ProposalResponse> responses = null ;
        try {
        	 responses = channel.sendTransactionProposal(tqr);
        	List<ProposalResponse> invalid = responses.stream().filter(res -> res.isInvalid()).collect(Collectors.toList());
        	if (!invalid.isEmpty()) {
        		
        		invalid.forEach(response -> {
        			System.out.println(response.getMessage());
        		});
        		
        	}
        } catch (ProposalException | InvalidArgumentException e) {
			e.printStackTrace();
		}
         
        try {
        	
        	 BlockEvent.TransactionEvent event = channel.sendTransaction(responses).get(60,TimeUnit.SECONDS);
        	 if (event.isValid()) {
        		 System.out.println(event.getTransactionID()+" transaction is valid ");
        	 }else {
        		 System.out.println(event.getTransactionID() + " transaction is invalid");
        	 }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}
        
    	
    }
	
    static void queryBlockChain(HFClient client) throws ProposalException, InvalidArgumentException {
        // get channel instance from client
        Channel channel = client.getChannel("mychannel");
        // create chaincode request
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
     //   TransactionProposalRequest trq=client.newTransactionProposalRequest();
        // build cc id providing the chaincode name. Version is omitted here.
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        qpr.setChaincodeID(tradeFinanceCCId);
        // CC function to be called
        qpr.setFcn("getContract");
        qpr.setArgs(new String[]{"100"});
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        // display response
        for (ProposalResponse pres : res) {
            String stringResponse = new String(pres.getChaincodeActionResponsePayload());
           // log.info(stringResponse);
            System.out.println(stringResponse);
        }
    }
	
    static Channel getChannel(HFClient client) throws InvalidArgumentException, TransactionException {
        // initialize channel
        // peer name and endpoint in fabcar network
        Peer peer1 = client.newPeer("peer0.importer.bridgeIt.com", "grpc://localhost:7051");
//        Peer peer2 = client.newPeer("peer0.exporter.bridgeIt.com", "grpc://localhost:8051");
//        Peer peer3 = client.newPeer("peer0.custom.bridgeIt.com", "grpc://localhost:9051");
//        Peer peer4 = client.newPeer("peer0.importer.bridgeIt.com", "grpc://localhost:10051");
//        Peer peer5 = client.newPeer("peer0.importerBank.bridgeIt.com", "grpc://localhost:11051");

        // eventhub name and endpoint in fabcar network
        EventHub eventHub1 = client.newEventHub("eventhub01", "grpc://localhost:7053");
//        EventHub eventHub2 = client.newEventHub("eventhub02", "grpc://localhost:8053");
//        EventHub eventHub3 = client.newEventHub("eventhub03", "grpc://localhost:9053");
//        EventHub eventHub4 = client.newEventHub("eventhub04", "grpc://localhost:10053");
//        EventHub eventHub5 = client.newEventHub("eventhub05", "grpc://localhost:11053");

        // orderer name and endpoint in fabcar network
        Orderer orderer = client.newOrderer("orderer.bridgeIt.com", "grpc://localhost:7050");
        // channel name in fabcar network
        Channel channel = client.newChannel("mychannel");
        channel.addPeer(peer1);
//        channel.addPeer(peer2);
//        channel.addPeer(peer3);
//        channel.addPeer(peer4);
//        channel.addPeer(peer5);
        channel.addEventHub(eventHub1);
//        channel.addEventHub(eventHub2);
//        channel.addEventHub(eventHub3);
//        channel.addEventHub(eventHub4);
//        channel.addEventHub(eventHub5);
        channel.addOrderer(orderer);
       
        //channel.registerBlockListener(chaincodeEventListener);
        channel.initialize();
        return channel;
    }
    static HFClient getHfClient() throws Exception {
        // initialize default cryptosuite
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        // setup the client
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(cryptoSuite);
        return client;
    }
	static HFCAClient getHFCaClient(String caUrl) {
		CryptoSuite suite = null;
		HFCAClient client = null;
		try {
			suite = CryptoSuite.Factory.getCryptoSuite();
			client = HFCAClient.createNewInstance(caUrl, null);
			client.setCryptoSuite(suite);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CryptoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return client;
	}
    static AppUser getUser(HFCAClient caClient, AppUser registrar, String userId) throws Exception {
        AppUser appUser = tryDeserialize(userId);
        if (appUser == null) {
            RegistrationRequest rr = new RegistrationRequest(userId, "org1");
            String enrollmentSecret = caClient.register(rr, registrar);
            Enrollment enrollment = caClient.enroll(userId, enrollmentSecret);
            appUser = new AppUser(userId, "importer", "ImporterMSP", enrollment);
            serialize(appUser);
        }
        return appUser;
    }
    static AppUser getAdmin(HFCAClient caClient) throws Exception {
        AppUser admin = tryDeserialize("admin");
        if (admin == null) {
            Enrollment adminEnrollment = caClient.enroll("admin", "adminpw");
            admin = new AppUser("admin", "importer", "ImporterMSP", adminEnrollment);
            serialize(admin);
        }
        return admin;
    }
	
    static void serialize(AppUser appUser) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
                Paths.get(appUser.getName() + ".jso")))) {
            oos.writeObject(appUser);
        }
    }
	
  static AppUser tryDeserialize(String name) throws Exception {
        if (Files.exists(Paths.get(name + ".jso"))) {
            return deserialize(name);
        }
        return null;
    }

    static AppUser deserialize(String name) throws Exception {
        try (ObjectInputStream decoder = new ObjectInputStream(
                Files.newInputStream(Paths.get(name + ".jso")))) {
            return (AppUser) decoder.readObject();
        }
    }

}
