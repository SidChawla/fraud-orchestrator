package com.fraudorchestrator.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

@Configuration
public class AwsConfiguration {

	@Bean
	public AWSStaticCredentialsProvider amazonAWSCredentialsProviderDevelopment() {
		return new AWSStaticCredentialsProvider(
				new BasicAWSCredentials("AKIA4U35JFXWKXTCNI6Q", "b06FEqlCSWcezx2O5NaMV+meEt7RoDFyil8NCghA"));
	}

}
