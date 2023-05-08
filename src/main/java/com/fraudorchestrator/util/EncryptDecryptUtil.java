package com.fraudorchestrator.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fraudorchestrator.service.FraudDetectorService;

public class EncryptDecryptUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(FraudDetectorService.class);

	private static SecretKeySpec secretKey;
	private static byte[] key;
	private static final String ALGORITHM = "AES";

	public static void prepareSecreteKey() {
		String myKey = "SECRET";
		MessageDigest sha = null;
		try {
			key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Exception in prepareSecreteKey() : ", e);
		}
	}

	public static String encrypt(String strToEncrypt) {
		try {
			prepareSecreteKey();
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			LOGGER.error("Exception in encrypt() method. : ", e);
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		try {
			prepareSecreteKey();
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			LOGGER.error("Exception in decrypt() method. : ", e);
		}
		return null;
	}

}
