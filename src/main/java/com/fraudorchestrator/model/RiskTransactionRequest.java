package com.fraudorchestrator.model;

public class RiskTransactionRequest {

	private String ipAdrress;
	private String email;
	private String billingState;
	private String userAgent;
	private String billingPostal;
	private String phoneNumber;
	private String billingAddress;

	public String getIpAdrress() {
		return ipAdrress;
	}

	public void setIpAdrress(String ipAdrress) {
		this.ipAdrress = ipAdrress;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getBillingPostal() {
		return billingPostal;
	}

	public void setBillingPostal(String billingPostal) {
		this.billingPostal = billingPostal;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

}
