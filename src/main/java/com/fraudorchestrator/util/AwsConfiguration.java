package com.fraudorchestrator.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AwsConfiguration {

	private String ENCRYPT_KEY = "GZYPDxsOq7XNeoNDlMDVSfWUYQD6BIAyuoz7HwazCKU=";
	private String ENCRYPT_SECRET = "AZ/YBf/cLZgPbx2kFRAYRMDmtWwFUNSqESY9JW5I8nlkw4K/oxp6Fmy2pFnbm4zS";

	@Bean
	public AWSStaticCredentialsProvider amazonAWSCredentialsProviderDevelopment() {
		return new AWSStaticCredentialsProvider(new BasicAWSCredentials(ENCRYPT_KEY, ENCRYPT_SECRET));
	}

	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

}
