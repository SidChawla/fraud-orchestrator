package com.fraudorchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fraudorchestrator.model.RiskTransactionRequest;
import com.fraudorchestrator.model.RiskTransactionResponse;
import com.fraudorchestrator.service.FraudDetectorService;

@RestController
@RequestMapping("/fraudorchestrator")
public class RiskController {

	@Autowired
	private FraudDetectorService fraudDetectorService;

	@GetMapping(path = "/ping")
	public String ping() {
		return "Ram Ram ji...";
	}

	@PostMapping(path = "/evaluate-risk")
	public RiskTransactionResponse evaluateRisk(
			@RequestHeader(value = "fraudDetector", required = false, defaultValue = "false") boolean fraudDetector,
			@RequestBody RiskTransactionRequest riskRequest) {

		return fraudDetectorService.evaluateRisk(fraudDetector, riskRequest);

	}

}
