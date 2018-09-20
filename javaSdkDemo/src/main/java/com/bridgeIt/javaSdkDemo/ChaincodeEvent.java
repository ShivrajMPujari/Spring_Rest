package com.bridgeIt.javaSdkDemo;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEventListener;

public class ChaincodeEvent implements ChaincodeEventListener{

	@Override
	public void received(String handle, BlockEvent blockEvent,
			org.hyperledger.fabric.sdk.ChaincodeEvent chaincodeEvent) {
		System.out.println(chaincodeEvent.getEventName());
		
	}

}
