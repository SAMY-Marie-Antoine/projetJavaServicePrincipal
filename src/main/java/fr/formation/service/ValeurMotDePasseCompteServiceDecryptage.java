package fr.formation.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class ValeurMotDePasseCompteServiceDecryptage {
	
	public static SecretKey convertStringToSecretKeyto(String encodedKey) {
	    byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
	    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	    return originalKey;
	}

	public static String decrypter(final byte[] donnees, SecretKey cle)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, cle);

		return new String(cipher.doFinal(donnees));
	}



}
