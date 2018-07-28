package com.bridgeIt.restDemo.model;

public class PaymentRequest {

	private int userID;
	private String itemId;
	private double discount;
	
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	@Override
	public String toString() {
		return "PaymentRequest [userID=" + userID + ", itemId=" + itemId + ", discount=" + discount + "]";
	}
	
	
	
	
}
