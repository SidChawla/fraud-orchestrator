package com.fraudorchestrator.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kafka.KafkaClientBuilder;

@Configuration
public class KafkaConfiguration {

	private final String BOOTSTRAP_SERVERS = "b-2.frauddetectorcluster.sysqrh.c6.kafka.us-east-1.amazonaws.com:9098,b-1.frauddetectorcluster.sysqrh.c6.kafka.us-east-1.amazonaws.com:9098";

	private String ENCRYPT_KEY = "GZYPDxsOq7XNeoNDlMDVSfWUYQD6BIAyuoz7HwazCKU=";
	private String ENCRYPT_SECRET = "AZ/YBf/cLZgPbx2kFRAYRMDmtWwFUNSqESY9JW5I8nlkw4K/oxp6Fmy2pFnbm4zS";

//	@Autowired
//	private AWSStaticCredentialsProvider awsCredentails;
//	
	@Bean
	public KafkaClient createKafkaClient() {
		KafkaClientBuilder builder = KafkaClient.builder().region(Region.of("us-east-1"))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
						.create(EncryptDecryptUtil.decrypt(ENCRYPT_KEY), EncryptDecryptUtil.decrypt(ENCRYPT_SECRET))));

		KafkaClient kafkaClient = builder.build();
		return kafkaClient;
	}

	@Bean
	public Producer<String, String> createKafkaProducer() {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.ACKS_CONFIG, "1");
		props.put(ProducerConfig.RETRIES_CONFIG, "3");
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
		props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "20000");
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "500");
		props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5000");
		props.setProperty("security.protocol", "SASL_SSL");
		props.setProperty("sasl.mechanism", "AWS_MSK_IAM");
		props.setProperty("sasl.jaas.config", "software.amazon.msk.auth.iam.IAMLoginModule required;");
		props.setProperty("sasl.client.callback.handler.class",
				"software.amazon.msk.auth.iam.IAMClientCallbackHandler");
		props.setProperty("client.dns.lookup", "use_all_dns_ips");

		Producer<String, String> producer = new KafkaProducer<>(props);

		return producer;

	}

}
