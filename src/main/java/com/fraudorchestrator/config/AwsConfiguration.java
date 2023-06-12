package com.fraudorchestrator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.frauddetector.AmazonFraudDetector;
import com.amazonaws.services.frauddetector.AmazonFraudDetectorClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fraudorchestrator.util.Constants;
import com.fraudorchestrator.util.EncryptDecryptUtil;

@Configuration
public class AwsConfiguration {

	@Bean
	public AWSStaticCredentialsProvider amazonAWSCredentialsProviderDevelopment() {
		return new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(EncryptDecryptUtil.decrypt(Constants.ENCRYPT_KEY),
						EncryptDecryptUtil.decrypt(Constants.ENCRYPT_SECRET)));
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public AmazonFraudDetector getAmazonFraudDetector() {
		Regions region = Regions.fromName("us-east-1");
		AmazonFraudDetector awsFrausDetector = AmazonFraudDetectorClientBuilder.standard()
				.withCredentials(amazonAWSCredentialsProviderDevelopment()).withRegion(region).build();
		return awsFrausDetector;
	}

	@Bean
	public AWSLambda getAWSLambda() {
		Regions region = Regions.fromName("us-east-1");
		AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
				.withCredentials(amazonAWSCredentialsProviderDevelopment()).withRegion(region);
		AWSLambda client = builder.build();
		return client;
	}

}
