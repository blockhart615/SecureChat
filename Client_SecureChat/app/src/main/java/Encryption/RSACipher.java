/**
 * use RSA for key transport
 * use AES for message encryption
 * use HMAC-SHA256 for message integrity, etc
 */

package Encryption;

import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

/**
 * Created by Brett on 12/1/2016.
 */

public class RSACipher {

	private final String RSA_ALGORITHM = "RSA";
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
	private Cipher cipher;

	public RSACipher() {
		//PUBLIC KEY INITIALIZATIONS
		try {
			keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
			keyGen.initialize(2048);
			keyPair = keyGen.generateKeyPair();
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		}
		catch (Exception e) {
			Log.d("No Such Algorithm: ", e.getMessage());
		}

	}

	/**
	 * Encrypt using RSA public key
	 * @param plainText
	 * @return
	 */
	public byte[] RSAEncrypt(String plainText) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
			return cipher.doFinal(plainText.getBytes("UTF8"));
		}
		catch (Exception e) {
			Log.d("Exception Occurred: ", e.getMessage());
			return null;
		}
	}

	/**
	 * Decrypt usiong RSA private key
	 * @param cipherText
	 * @return
	 */
	public byte[] RSADecrypt(String cipherText) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			return cipher.doFinal(cipherText.getBytes("UTF8"));
		}
		catch (Exception e) {
			Log.d("Exception Occurred: ", e.getMessage());
			return null;
		}
	}

	public void AESEncrypt() {

	}

	public void AESDecrypt() {

	}


}
