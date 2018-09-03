package com.bridgeIt.javaSdkDemo;

import java.io.Serializable;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

public class AppUser implements User,Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment;
    private String mspId;
	
    public AppUser() {
        // no-arg constructor
    }

    public AppUser(String name, String affiliation, String mspId, Enrollment enrollment) {
        this.name = name;
        this.affiliation = affiliation;
        this.enrollment = enrollment;
        this.mspId = mspId;
    }
	
	public String getName() {
		
		return name;
	}

	public Set<String> getRoles() {

		return roles;
	}

	public String getAccount() {

		return account;
	}

	public String getAffiliation() {

		return affiliation;
	}

	public Enrollment getEnrollment() {

		return enrollment;
	}

	public String getMspId() {

		return mspId;
	}
	
	
    @Override
    public String toString() {
        return "AppUser{" +
                "name='" + name + '\'' +
                "\n, roles=" + roles +
                "\n, account='" + account + '\'' +
                "\n, affiliation='" + affiliation + '\'' +
                "\n, enrollment=" + enrollment +
                "\n, mspId='" + mspId + '\'' +
                '}';
    }
}
