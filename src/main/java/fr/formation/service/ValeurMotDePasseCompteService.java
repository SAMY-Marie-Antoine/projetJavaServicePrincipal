package fr.formation.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

@Service
public class ValeurMotDePasseCompteService {
	
	
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(n);
	    SecretKey originalKey = keyGenerator.generateKey();
	    return originalKey;
	}
	
	public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
	    byte[] rawData = secretKey.getEncoded();
	    String encodedKey = Base64.getEncoder().encodeToString(rawData);
	    return encodedKey;
	}

	
	public static byte[] encrypter(final String message, SecretKey cle)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, cle);
		byte[] donnees = message.getBytes();

		return cipher.doFinal(donnees);
	}
	
	
	
	





}
