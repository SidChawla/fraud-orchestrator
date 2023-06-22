package com.fraudorchestrator.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);

	@Autowired
	private FraudDetectorService fraudDetectorService;

	@GetMapping(path = "/ping")
	public String ping() {
		String hostAddress = null;
		LOGGER.info("Inside /ping endpoint.");
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
			LOGGER.info("hostAddress = " + hostAddress);
		} catch (UnknownHostException e) {
			LOGGER.error("UnknownHostException occured.", e);
		}
		return ("Ram Ram ji..." + hostAddress);
	}

	@PostMapping(path = "/evaluate-risk")
	public RiskTransactionResponse evaluateRisk(
			@RequestHeader(value = "fraudDetector", required = false, defaultValue = "false") boolean fraudDetector,
			@RequestBody RiskTransactionRequest riskRequest) {

		LOGGER.info("Inside /evaluate-risk endpoint.");
		return fraudDetectorService.evaluateRisk(fraudDetector, riskRequest);

	}

}
