package com.fraudorchestrator.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AwsConfiguration {

	private String AWS_ACCESS_KEY_ID = "AKIA4U35JFXWKXTCNI6Q";
	private String AWS_SECRET_ACCESS_KEY = "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";

	@Bean
	public AWSStaticCredentialsProvider amazonAWSCredentialsProviderDevelopment() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY));
	}
	
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

}
