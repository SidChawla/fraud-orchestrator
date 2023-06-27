package com.fraudorchestrator.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.frauddetector.AmazonFraudDetector;
import com.amazonaws.services.frauddetector.model.Entity;
import com.amazonaws.services.frauddetector.model.GetEventPredictionRequest;
import com.amazonaws.services.frauddetector.model.GetEventPredictionResult;
import com.amazonaws.services.frauddetector.model.ModelScores;
import com.amazonaws.services.frauddetector.model.RuleResult;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudorchestrator.model.RiskTransactionRequest;
import com.fraudorchestrator.model.RiskTransactionResponse;
import com.fraudorchestrator.util.Constants;

@Service
public class FraudDetectorService {

	private static Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);

	@Autowired
	private AmazonFraudDetector amazonFraudDetector;

	@Autowired
	private AWSLambda awsLambda;

	@Autowired
	private ObjectMapper objectMapper;

//	@Autowired
//	private Producer<String, String> kafkaProducer;

	public RiskTransactionResponse evaluateRisk(boolean fraudDetector, RiskTransactionRequest riskRequest) {
		RiskTransactionResponse riskResponse = new RiskTransactionResponse();
		if (fraudDetector) {
			try {
				GetEventPredictionRequest eventPredictionRequest = createEventPredictionRequest(riskRequest);

				GetEventPredictionResult eventPredictionResult = amazonFraudDetector
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
					setDatafromRiskRequest(riskRequest, riskResponse);
				}

			} catch (Exception e) {
				LOGGER.error("Exception occured in FraudDetector Orchestrator ::: ", e);
			}
		} else {
			createDummyRiskResponse(riskResponse);
		}

		try {
			String responseJson = objectMapper.writeValueAsString(riskResponse);

			LOGGER.info("About to invoke Lambda...");
			// Invoking Lambda function asynchronously.
			awsLambda.invoke(new InvokeRequest().withFunctionName(Constants.LAMBDA_FUNCTION_NAME)
					.withInvocationType(InvocationType.Event) // asynchronous
					.withPayload(responseJson));

			LOGGER.info("Invoked Lambda async successfully...");
		} catch (Exception e) {
			LOGGER.error("Exception occured in Lamda part :::", e);
		}
		return riskResponse;
	}

	private void setDatafromRiskRequest(RiskTransactionRequest riskRequest, RiskTransactionResponse riskResponse) {
		riskResponse.setBillingAddress(riskRequest.getBillingAddress());
		riskResponse.setBillingPostal(riskRequest.getBillingPostal());
		riskResponse.setBillingState(riskRequest.getBillingState());
		riskResponse.setIpAdrress(riskRequest.getIpAdrress());
		// masking email.
		riskResponse.setEmail(riskRequest.getEmail().replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*"));
		// masking phoneNumber.
		riskResponse.setPhoneNumber(riskRequest.getPhoneNumber().replaceAll("\\d(?=\\d{4})", "*"));
		riskResponse.setUserAgent(riskRequest.getUserAgent());
	}

	private GetEventPredictionRequest createEventPredictionRequest(RiskTransactionRequest riskRequest) {
		GetEventPredictionRequest eventPredictionRequest = new GetEventPredictionRequest();
		eventPredictionRequest.setDetectorId(Constants.DETECTOR_ID);
		eventPredictionRequest.setDetectorVersionId("1");
		Entity e = new Entity();
		e.setEntityType(Constants.ENTITY_TYPE);
		e.setEntityId(Constants.ENTITY_ID);
		List<Entity> entities = new ArrayList<>();
		entities.add(e);
		eventPredictionRequest.setEntities(entities);

		Map<String, String> eventVariables = createEventVariableMap(riskRequest);

		eventPredictionRequest.setEventVariables(eventVariables);
		eventPredictionRequest.setEventTypeName(Constants.ENTITY_TYPE_NAME);
		eventPredictionRequest.setEventTimestamp("2023-05-04T23:00:03Z");
		eventPredictionRequest.setEventId(Constants.EVENT_ID);
		return eventPredictionRequest;
	}

	private Map<String, String> createEventVariableMap(RiskTransactionRequest riskRequest) {
		Map<String, String> eventVariables = new HashMap<>();
		eventVariables.put("ip_address", riskRequest.getIpAdrress());
		eventVariables.put("email_address", riskRequest.getEmail());
		eventVariables.put("billing_state", riskRequest.getBillingState());
		eventVariables.put("user_agent", riskRequest.getUserAgent());
		eventVariables.put("billing_postal", riskRequest.getBillingPostal());
		eventVariables.put("phone_number", riskRequest.getPhoneNumber());
		eventVariables.put("billing_address", riskRequest.getBillingAddress());
		return eventVariables;
	}

	private void createDummyRiskResponse(RiskTransactionResponse riskResponse) {
		riskResponse.setBillingAddress("Mayur Vihar");
		riskResponse.setBillingPostal("110096");
		riskResponse.setBillingState("DELHI");
		riskResponse.setEmail("sidchawla1990@gmail.com");
		riskResponse.setIpAdrress("192.168.1.1");
		riskResponse.setPhoneNumber("9711906826");
		riskResponse.setUserAgent("Lenovo i5 Laptop");
		riskResponse.setRiskScore("299");
		riskResponse.setRiskOutcome("risk-low");
	}

}
