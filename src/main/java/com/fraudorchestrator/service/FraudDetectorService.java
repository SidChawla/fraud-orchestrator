package com.fraudorchestrator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.frauddetector.AmazonFraudDetector;
import com.amazonaws.services.frauddetector.AmazonFraudDetectorClientBuilder;
import com.amazonaws.services.frauddetector.model.Entity;
import com.amazonaws.services.frauddetector.model.GetEventPredictionRequest;
import com.amazonaws.services.frauddetector.model.GetEventPredictionResult;
import com.amazonaws.services.frauddetector.model.ModelScores;
import com.amazonaws.services.frauddetector.model.RuleResult;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudorchestrator.model.RiskTransactionRequest;
import com.fraudorchestrator.model.RiskTransactionResponse;
import com.fraudorchestrator.util.EncryptDecryptUtil;

@Service
public class FraudDetectorService {

	private static Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);

	@Autowired
	private AWSStaticCredentialsProvider aWSStaticCredentialsProvider;
	
	@Autowired
	private ObjectMapper objectMapper;

	public RiskTransactionResponse evaluateRisk(boolean fraudDetector, RiskTransactionRequest riskRequest) {
		RiskTransactionResponse riskResponse = new RiskTransactionResponse();
		Regions region = Regions.fromName("us-east-1");
		if (fraudDetector) {
			try {
				AmazonFraudDetector awsFrausDetector = AmazonFraudDetectorClientBuilder.standard()
						.withCredentials(aWSStaticCredentialsProvider).withRegion(region).build();
				
				GetEventPredictionRequest eventPredictionRequest = new GetEventPredictionRequest();
				eventPredictionRequest.setDetectorId("techevent_frauddetector");
				eventPredictionRequest.setDetectorVersionId("1");
				Entity e = new Entity();
				e.setEntityType("customer_transaction");
				e.setEntityId("unknown");
				List<Entity> entities = new ArrayList<>();
				entities.add(e);
				eventPredictionRequest.setEntities(entities);

				Map<String, String> eventVariables = new HashMap<>();
				eventVariables.put("ip_address", riskRequest.getIpAdrress());
				eventVariables.put("email_address", riskRequest.getEmail());
				eventVariables.put("billing_state", riskRequest.getBillingState());
				eventVariables.put("user_agent", riskRequest.getUserAgent());
				eventVariables.put("billing_postal", riskRequest.getBillingPostal());
				eventVariables.put("phone_number", riskRequest.getPhoneNumber());
				eventVariables.put("billing_address", riskRequest.getBillingAddress());

				eventPredictionRequest.setEventVariables(eventVariables);
				eventPredictionRequest.setEventTypeName("frauddetection_demo");
				eventPredictionRequest.setEventTimestamp("2023-05-04T23:00:03Z");
				eventPredictionRequest.setEventId("sidchawla-runtest-event-9ededa24c2");

				GetEventPredictionResult eventPredictionResult = awsFrausDetector
						.getEventPrediction(eventPredictionRequest);

				if (null != eventPredictionResult) {
					List<ModelScores> modelScoresList = eventPredictionResult.getModelScores();
					for (ModelScores modelScores : modelScoresList) {
						LOGGER.info("###### modelScores.getScores() === " + modelScores.getScores());
						riskResponse.setRiskScore(
								modelScores.getScores().get("frauddetection_model1_insightscore").toString());
					}

					List<RuleResult> ruleResults = eventPredictionResult.getRuleResults();

					for (RuleResult ruleResult : ruleResults) {
						LOGGER.info("###### ruleResult.getOutcomes() === " + ruleResult.getOutcomes());
						riskResponse.setRiskOutcome(ruleResult.getOutcomes().get(0));
					}
				}

			} catch (Exception e) {
				LOGGER.error("Exception occured in FraudDetector part ::: ", e);
			}
		}

		LOGGER.info("About to invoke lambda...");
		try {
			String responseJson = objectMapper.writeValueAsString(riskResponse);

			AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
					.withCredentials(aWSStaticCredentialsProvider).withRegion(region);
			AWSLambda client = builder.build();
			InvokeRequest req = new InvokeRequest().withFunctionName("event-manager").withPayload(responseJson);
			client.invoke(req);
			LOGGER.info("Invoked lambda successfully...");
		} catch (Exception e) {
			LOGGER.error("Exception occured in Lamda part :::", e);
		}
		return riskResponse;
	}
}
