package com.fraudorchestrator.model;

public class RiskTransactionResponse {

	private String riskScore;
	private String riskOutcome;

	public String getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}

	public String getRiskOutcome() {
		return riskOutcome;
	}

	public void setRiskOutcome(String riskOutcome) {
		this.riskOutcome = riskOutcome;
	}

}
