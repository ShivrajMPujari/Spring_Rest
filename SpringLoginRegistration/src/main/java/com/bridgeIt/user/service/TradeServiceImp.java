package com.bridgeIt.user.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;

import com.bridgeIt.user.model.TradeUser;

public class TradeServiceImp implements TradeService {

	@Autowired
	HFCAClient caClient;
	

	
	@Override
	public TradeUser getAdmin(HFCAClient caClient) {
        TradeUser admin = tryDeserializeAdmin("admin");
        if (admin == null) {
            Enrollment adminEnrollment = null;
			try {
				adminEnrollment = caClient.enroll("admin", "adminpw");
			} catch (EnrollmentException | InvalidArgumentException e) {
				e.printStackTrace();
			}
            admin = new TradeUser("admin", "importer", "ImporterMSP", adminEnrollment);
            serializeAdmin(admin);
        }
        return admin;
    }

	@Override
	public TradeUser tryDeserializeAdmin(String name) {
        if (Files.exists(Paths.get(name + ".jso"))) {
            return deSerializeAdmin(name);
        }
        return null;
    }

	@Override
	public TradeUser deSerializeAdmin(String name) {
        try (ObjectInputStream decoder = new ObjectInputStream(
                Files.newInputStream(Paths.get(name + ".jso")))) {
            return (TradeUser) decoder.readObject();
        } catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
    }

	@Override
	public void serializeAdmin(TradeUser tradeUser) {
		try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
                Paths.get(tradeUser.getName() + ".jso")))) {
            oos.writeObject(tradeUser);
        } catch (IOException e) {

			e.printStackTrace();
		}
		
	}

	

}
