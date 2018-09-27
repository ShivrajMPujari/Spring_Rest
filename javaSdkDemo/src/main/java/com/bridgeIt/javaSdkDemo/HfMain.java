package com.bridgeIt.javaSdkDemo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
    
    static  String FILENAME ;
    public static void main(String[] args) throws Exception {
/*
    	Path path = Paths.get("//home//bridgelabz//Downloads//Sample.mp3");
    	
    	byte [] fileByte = Files.readAllBytes(path);
    	
    //	String fileBase64String = Base64.getEncoder().encodeToString(fileByte);
    	
    	System.out.println("filebytes:-  ---------------");
    	//System.out.println(fileByte);
    //	System.out.println(Arrays.toString(fileByte));
    //	System.out.println("file base64:- --------------");	
    //	System.out.println(fileBase64String);
    	Path path1 = Paths.get("//home//bridgelabz//Downloads//Sample.mp3");
    	System.out.println(path1.getFileName().toString());
    	FILENAME = path1.getFileName().toString();
    	 Path path2 = Paths.get("//home//bridgelabz//Documents//"+FILENAME);
    	    try {
    					Files.write(path2,fileByte );
    					System.out.println("success");
    				} catch (IOException e) {
    					
    					e.printStackTrace();
    				}*/
/*    	
    	byte[] bArray = {1,1,1,1,1,1,0,0,0,1,0,1,0,1,1,0,1};
    	splitByte(bArray);
    	
    	byte[] bArray1 = {1,1,1,1,1,1,0,0};
    	byte[] bArray2 = {0, 1, 0, 1, 0, 1, 1, 0, 1};
    	mergeByte(bArray1,bArray2);*/
    	
    	
    	
    	
        // create fabric-ca client
        HFCAClient caClient = getHFCaClient("http://localhost:7054");

        // enroll or load admin
        AppUser admin = getAdmin(caClient);
       // log.info(admin);
        System.out.println(admin);
        // register and enroll new user
        AppUser appUser = getUser(caClient, admin, "hfuser");
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
//        System.out.println("querying.....");
        // call query blockchain example
        //queryBlockChain(client);
        
        
        
//        transactionInvokeBlockChain(client,"Sample.mp3");
//        transactionInvokeBlockChain(client,"letterOfCredit.jpg");
       // transactionInvokeBlockChain(client,"billOfLading.jpg");
     //   transactionInvokeBlockChain(client,"picblock.jpg");
        System.out.println("querying.....");
//        queryBlockChain(client,"Sample.mp3");
//        queryBlockChain(client,"letterOfCredit.jpg");
    //    queryBlockChain(client,"billOfLading.jpg");
   //     queryBlockChain(client,"picblock.jpg");
        
        
        List<byte[]> list = splitFile("Sample640.mp4");
        byte [] arr1 = list.get(0);
		byte [] arr2 = list.get(1);
		
	//	System.out.println(Arrays.toString(arr2));
		
		System.out.println(arr2.length);
		
		transactionSplitBC(client,arr1,"split1");
		
	transactionSplitBC(client,arr2,"split2");
		
		String base1 = querySplitsInBlockChain(client,"split1");
		String base2 = querySplitsInBlockChain(client,"split2");
		
		mergeBase64(base1, base2);
    }
    
    
    static List<byte[]> splitFile(String fileName) {
    	Path path = Paths.get("//home//bridgelabz//Downloads//"+fileName);
    	System.out.println(path.getFileName().toString());
    	FILENAME = path.getFileName().toString();
    	byte[] fileByte = null;
		try {
			fileByte = Files.readAllBytes(path);
			int len =fileByte.length;
			System.out.println(len +" length of file ...");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
		
		List <byte[]> list = splitByte(fileByte);
		return list;
		
    }
    
    
    static void transactionSplitBC(HFClient client,byte[] arr,String name) {
    	

		
		
		
    	String fileBase64String1 = Base64.getEncoder().encodeToString(arr);
    
    	System.out.println("file base64:- --------------");	
    	//System.out.println(fileBase64String);
    
        Channel channel = client.getChannel("mychannel");
        TransactionProposalRequest	tqr = client.newTransactionProposalRequest();
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        tqr.setChaincodeID(tradeFinanceCCId);
        tqr.setFcn("setFile");
        tqr.setArgs(new String[] {name,fileBase64String1});
        Collection<ProposalResponse> responses = null ;
        try {
        	 responses = channel.sendTransactionProposal(tqr);
        	List<ProposalResponse> invalid = responses.stream().filter(res -> res.isInvalid()).collect(Collectors.toList());
        	if (!invalid.isEmpty()) {
        		
        		invalid.forEach(response -> {
        			System.out.println(response.getMessage());
        			System.out.println("--------");
        		});
        		
        	}
        } catch (ProposalException | InvalidArgumentException e) {
			e.printStackTrace();
		}
         
        try {
        	org.hyperledger.fabric.sdk.ChaincodeEvent cevent  ;
        	
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
    
    static void transactionInvokeBlockChain(HFClient client,String fileName) throws JsonProcessingException, InvalidArgumentException {
    	
    	
    	Path path = Paths.get("//home//bridgelabz//Downloads//"+fileName);
    	System.out.println(path.getFileName().toString());
    	FILENAME = path.getFileName().toString();
    	byte[] fileByte = null;
		try {
			fileByte = Files.readAllBytes(path);
			int len =fileByte.length;
			System.out.println(len +" length of file ...");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	String fileBase64String = Base64.getEncoder().encodeToString(fileByte);
    	
    	System.out.println("file base64:- --------------");	
    	//System.out.println(fileBase64String);
    	String fileStr = new String(fileByte);
        Channel channel = client.getChannel("mychannel");
        TransactionProposalRequest	tqr = client.newTransactionProposalRequest();
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        tqr.setChaincodeID(tradeFinanceCCId);
        tqr.setFcn("setFile");
        tqr.setArgs(new String[] {FILENAME,fileBase64String});
        Collection<ProposalResponse> responses = null ;
        try {
        	 responses = channel.sendTransactionProposal(tqr);
        	List<ProposalResponse> invalid = responses.stream().filter(res -> res.isInvalid()).collect(Collectors.toList());
        	if (!invalid.isEmpty()) {
        		
        		invalid.forEach(response -> {
        			System.out.println(response.getMessage());
        			System.out.println("--------");
        		});
        		
        	}
        } catch (ProposalException | InvalidArgumentException e) {
			e.printStackTrace();
		}
         
        try {
        	org.hyperledger.fabric.sdk.ChaincodeEvent cevent  ;
        	
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
	
    static void queryBlockChain(HFClient client,String fileName) throws ProposalException, InvalidArgumentException {
        // get channel instance from client
        Channel channel = client.getChannel("mychannel");
        // create chaincode request
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
     //   TransactionProposalRequest trq=client.newTransactionProposalRequest();
        // build cc id providing the chaincode name. Version is omitted here.
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        qpr.setChaincodeID(tradeFinanceCCId);
        // CC function to be called
        qpr.setFcn("getFile");
        qpr.setArgs(new String[]{fileName});
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        // display response
        
        Path path = Paths.get("//home//bridgelabz//Documents//"+fileName);
       
        
        for (ProposalResponse pres : res) {
            byte [] filebyteBC =pres.getChaincodeActionResponsePayload();
            String newFileStrBc = new String(filebyteBC);
           
           // log.info(stringResponse);
         byte [] fileBytes = Base64.getDecoder().decode(newFileStrBc);
            try {
				Files.write(path,  fileBytes);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
            System.out.println("success");
        }
        
    }
    
    
    static String querySplitsInBlockChain(HFClient client,String fileName) throws ProposalException, InvalidArgumentException {
        // get channel instance from client
        Channel channel = client.getChannel("mychannel");
        // create chaincode request
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
     //   TransactionProposalRequest trq=client.newTransactionProposalRequest();
        // build cc id providing the chaincode name. Version is omitted here.
        ChaincodeID tradeFinanceCCId = ChaincodeID.newBuilder().setName("tradefinancecc").build();
        qpr.setChaincodeID(tradeFinanceCCId);
        // CC function to be called
        qpr.setFcn("getFile");
        qpr.setArgs(new String[]{fileName});
        Collection<ProposalResponse> res = channel.queryByChaincode(qpr);
        // display response
        
        Path path = Paths.get("//home//bridgelabz//Documents//"+fileName);
       
        String newFileStrBc =null;
        for (ProposalResponse pres : res) {
            byte [] filebyteBC =pres.getChaincodeActionResponsePayload();
            newFileStrBc = new String(filebyteBC);
          
            System.out.println("success");
            return newFileStrBc;
        }
		return newFileStrBc;
        
    }
    
    static void mergeBase64(String base1,String base2) {
    	
    	 byte [] fileBytes1 = Base64.getDecoder().decode(base1);
    	 byte [] fileBytes2 = Base64.getDecoder().decode(base2);
    	
    	 byte [] fileBytes=  mergeByte(fileBytes1, fileBytes2);
    	 
    	 Path path = Paths.get("//home//bridgelabz//Documents//Sample640.mp4");
    	    try {
    					Files.write(path,  fileBytes);
    				} catch (IOException e) {
    					
    					e.printStackTrace();
    				}
    	            System.out.println("success");
    	 
    	 
    	
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
        ChaincodeEventListener chaincodeEventListener = new ChaincodeEvent();
        Pattern pattern = Pattern.compile(".*");
       // Pattern pattern2 = Pattern.compile("event1");
        String data = channel.registerChaincodeEventListener(pattern, Pattern.compile(Pattern.quote("event1")), chaincodeEventListener);
        System.out.println(data);
        String data1 = channel.registerChaincodeEventListener(pattern, Pattern.compile(Pattern.quote("event2")), chaincodeEventListener);
      System.out.println(data1);
        //  ChaincodeEvent listener = new ChaincodeEvent();
//        channel.registerChaincodeEventListener(Pattern.compile(".*"),
//                Pattern.compile(Pattern.quote("event1")),listener );
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
    
    static List<byte[]> splitByte(byte[] data) {
    	
    	
    	int len = data.length;
    	int len1 = data.length/2;
    	int len2 = len-len1;
    	
    	byte [] data1 =new byte[len1];
    	byte [] data2 = new byte[len2];
    	
    	
    	for(int i=0;i<len1;i++) {
    					
    			data1[i]=data[i];
    		
    	}
    	
    	int index = 0;
    	for(int j=len1;j<data.length;j++) {
    		
    		data2[index++] = data[j];
    		
    	}
    	
    	List<byte[]> list = new ArrayList<byte[]>();
    	list.add(data1);
    	list.add(data2);
		return list;
    	
    }
    
    public static byte[] mergeByte(byte[] data1 ,byte[] data2 ) {
    	
    	
    	ArrayList<Byte> list = new ArrayList<Byte>();
    	for (int i = 0; i < data1.length; i++) {
    		list.add(data1[i]);
		}
    	for (int j = 0; j < data2.length; j++) {
    		list.add(data2[j]);
		}
    	
    	byte[]	mergedArray = new byte [data1.length+data2.length]; 
    	
    	for (int i = 0; i < mergedArray.length; i++) {
		
    		mergedArray[i]=list.get(i);
    		
		}
    
    	return mergedArray;
    }
    
	static HFCAClient getHFCaClient(String caUrl) {
		CryptoSuite suite = null;
		HFCAClient client = null;
		try {
			suite = CryptoSuite.Factory.getCryptoSuite();
			client = HFCAClient.createNewInstance(caUrl, null);
			client.setCryptoSuite(suite);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (CryptoException e) {
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
                Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+appUser.getName() + ".jso")))) {
            oos.writeObject(appUser);
        }
    }
	
  static AppUser tryDeserialize(String name) throws Exception {
        if (Files.exists(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso"))) {
            return deserialize(name);
        }
        return null;
    }

    static AppUser deserialize(String name) throws Exception {
        try (ObjectInputStream decoder = new ObjectInputStream(
                Files.newInputStream(Paths.get("//home//bridgelabz//Documents//workspace-sts-3.9.5.RELEASE//"+name + ".jso")))) {
            return (AppUser) decoder.readObject();
        }
    }

}
