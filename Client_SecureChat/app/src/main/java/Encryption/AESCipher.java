package Encryption;

/**
 * Created by Brett on 12/1/2016.
 */

import android.util.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.util.encoders.Base64;

public class AESCipher {

	private final String ALGORITHM = "AES";
	private final String HMAC_ALGORITHM = "HmacSHA256";
	private final String ENCRYPTION_MODE = "CTR";
	private final String PADDING = "NoPadding";
	private final int KEY_SIZE = 256;
	private final int HASH_SIZE = 32; //256 bits = 32 bytes
	private final int IV_LENGTH = 16; //128 bit IV

	KeyGenerator keyGen;
	private SecretKey encryptionKey, integrityKey;
	private Cipher cipher;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public AESCipher() {
		try {
			cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" + PADDING);

			keyGen = KeyGenerator.getInstance(ALGORITHM);
			keyGen.init(KEY_SIZE);
			integrityKey = keyGen.generateKey();
		}
		catch (NoSuchAlgorithmException e) {
			Log.w("No Such Algorithm: ", e.getMessage());
		}
		catch (NoSuchPaddingException e) {
			Log.w("No Such Padding: ", e.getMessage());
		}
	}

	/**
	 * @return the encryption key
     */
	public Key getEncryptionKey() {
		return encryptionKey;
	}

	/**
	 * @return the Integrity Key
     */
	public Key getIntegrityKey() {
		return integrityKey;
	}

	/**
	 *
	 * @param plainText raw
	 * @return encrypted cipherText as a string, with IV prepended
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String encrypt(String plainText)
			throws Exception{

		//Generate new key for every message
		encryptionKey = keyGen.generateKey();
		IvParameterSpec iv = generateIV();

		cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, iv);
		byte[] encrypted = cipher.doFinal(plainText.getBytes());

		byte[] cipherText = new byte[IV_LENGTH + encrypted.length];

		//append IV to the front of the ciphertext
		System.arraycopy(iv.getIV(), 0, cipherText, 0, iv.getIV().length);
		System.arraycopy(encrypted, 0, cipherText, IV_LENGTH, encrypted.length);

		//add HMAC tag to end of cipherText
		byte[] hash = performHMAC(encrypted);

		//cipherHash is a byte array that is the cipherText || hash
		byte[] cipherHash = new byte[hash.length + cipherText.length];
		System.arraycopy(cipherText, 0, cipherHash, 0, cipherText.length);
		System.arraycopy(hash, 0, cipherHash, cipherText.length, hash.length);

		// concatenate the encryption key to the end of the ciphertext
		byte[] keyBytes = encryptionKey.getEncoded();

		// byte array of everything combined
		// IV || Cipher Text || HMAC || Key
		byte[] combined = new byte[cipherHash.length + keyBytes.length];

		System.arraycopy(cipherHash, 0, combined, 0, cipherHash.length);
		System.arraycopy(keyBytes, 0, combined, cipherHash.length, keyBytes.length);

		return Base64.toBase64String(combined);
	}

	/**
	 *
	 * @param cipherText the cipher text to be decrypted
	 * @return decrypted plaintext as a string
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(String cipherText)
			throws Exception{

		//store ciphertext
		byte[] decodedBytes = Base64.decode(cipherText);
		//start new byte array to hold encryption key
		byte[] decryptionBytes = new byte[KEY_SIZE/8];
		//get length of IV + Message. Have to divide by 8 to convert from bits to bytes
		int IVandMessage = decodedBytes.length - KEY_SIZE/8;

		//get decryption key out of ciphertext
		System.arraycopy(decodedBytes, IVandMessage, decryptionBytes, 0, decryptionBytes.length);
		Key decryptionKey = new SecretKeySpec(decryptionBytes, ALGORITHM);

		//get IV out of ciphertext
		byte[] iv = new byte[IV_LENGTH];
		System.arraycopy(decodedBytes, 0, iv, 0, IV_LENGTH);

		//get length of message. have to remove IV length and key bytes
		int messageLength = decodedBytes.length - IV_LENGTH - decryptionBytes.length - HASH_SIZE;
		byte[] messageBytes = new byte[messageLength];
		System.arraycopy(decodedBytes, IV_LENGTH, messageBytes, 0, messageLength);

		//get HASH tag out of ciphertext
		byte[] hash = new byte[HASH_SIZE];
		System.arraycopy(decodedBytes, IV_LENGTH + messageBytes.length, hash, 0, HASH_SIZE);

		//Checks if the tag matches
		if (Arrays.equals(hash, performHMAC(messageBytes))){
			//Decrypt the cipher text
			cipher.init(Cipher.DECRYPT_MODE, decryptionKey, new IvParameterSpec(iv));
			byte[] plainText = cipher.doFinal(messageBytes);

			return new String(plainText);
		}
		else {
			System.out.println("HMAC DID NOT MATCH!");
			System.out.println("Original HMAC: " + Base64.toBase64String(hash));
			System.out.println("Calculated HMAC: " + Base64.toBase64String(performHMAC(messageBytes)));
			return null;
		}


	}

	/**
	 * Generates an IV for encryption
	 * @return iv
     */
	private IvParameterSpec generateIV() {
		byte[] ivByte = new byte[IV_LENGTH];

		SecureRandom rand = new SecureRandom();
		rand.nextBytes(ivByte);

		return new IvParameterSpec(ivByte);
	}

	/**
	 * Calculates HMAC SHA256 on the cipher text
	 * @param cipherText cipher text being tagged
	 * @return hash
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
     */
	private byte[] performHMAC(byte[] cipherText) throws NoSuchAlgorithmException, InvalidKeyException{

		//create new mac and hash the cipherText
		Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
		hmac.init(integrityKey);

		return hmac.doFinal(cipherText);
	}
}
