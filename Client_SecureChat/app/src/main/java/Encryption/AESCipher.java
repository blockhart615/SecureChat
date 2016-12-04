package Encryption;

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

	private KeyGenerator keyGen;
	private SecretKey encryptionKey, integrityKey;
	private Cipher cipher;
	private RSACipher rsaCipher;

	/**
	 * DEFAULT CONSTRUCTOR
	 */
	public AESCipher() {
		try {
			cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" + PADDING);

			keyGen = KeyGenerator.getInstance(ALGORITHM);
			keyGen.init(KEY_SIZE);
			integrityKey = keyGen.generateKey();
			rsaCipher = new RSACipher();
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

		//Generate new key and IV for every message
		encryptionKey = keyGen.generateKey();
		IvParameterSpec iv = generateIV();

		//encrypt the plainText
		cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, iv);

		/**
		 * IV + ENCRYPTED + HMACtag + enc(KEY_E + KEY_I)
		 */
		byte[]ivBytes = iv.getIV();
		byte[] encrypted = cipher.doFinal(plainText.getBytes());
		byte[] tag = performHMAC(encrypted, integrityKey);
		byte[] key_e = encryptionKey.getEncoded();
		byte[] key_i = integrityKey.getEncoded();
		int startPos = 0;
		byte[] cipherText = new byte[IV_LENGTH + encrypted.length + HASH_SIZE + key_e.length + key_i.length];

		System.out.println("KEY_e: " + Base64.toBase64String(key_e));
		System.out.println("KEY_i: " + Base64.toBase64String(key_i));

		//build out cipherText byte array to be sent
		// add IV
		System.arraycopy(ivBytes, 0, cipherText, startPos, ivBytes.length);
		startPos += ivBytes.length;

		// add encrypted message
		System.arraycopy(encrypted, 0, cipherText, startPos, encrypted.length);
		startPos += encrypted.length;

		// add HMAC tag
		System.arraycopy(tag, 0, cipherText, startPos, tag.length);
		startPos += tag.length;

		//add encryption key and integrity key
		System.arraycopy(key_e, 0, cipherText, startPos, key_e.length);
		startPos += key_e.length;
		System.arraycopy(key_i, 0, cipherText, startPos, key_i.length);

		return Base64.toBase64String(cipherText);
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

		/**
		 * HAVE TO GET EACH THING OUT OF CIPHERTEXT
		 * IV + cipherText + HMAC + Key_e + Key_i
		 */
		byte[] decodedBytes = Base64.decode(cipherText); //the WHOLE package
		byte[] iv = new byte[IV_LENGTH];
		byte[] encryptedMessage = new byte[decodedBytes.length - IV_LENGTH - HASH_SIZE - (2*KEY_SIZE/8)];
		byte[] hash = new byte[HASH_SIZE];
		byte[] encryptionKeyBytes = new byte[KEY_SIZE/8];
		byte[] integrityKeyBytes = new byte[KEY_SIZE/8];
		int startingPos = 0;

		//get IV out of decoded bytes
		System.arraycopy(decodedBytes, startingPos, iv, 0, IV_LENGTH);
		startingPos += IV_LENGTH;

		//get encrypted message out of decoded bytes
		System.arraycopy(decodedBytes, startingPos, encryptedMessage, 0, encryptedMessage.length);
		startingPos += encryptedMessage.length;

		//get HMAC tag out of decoded bytes
		System.arraycopy(decodedBytes, startingPos, hash, 0, hash.length);
		startingPos += hash.length;

		//get key_e out of decoded bytes
		System.arraycopy(decodedBytes, startingPos, encryptionKeyBytes, 0, encryptionKeyBytes.length);
		startingPos += encryptionKeyBytes.length;

		//get key_i out of decoded bytes
		System.arraycopy(decodedBytes, startingPos, integrityKeyBytes, 0, integrityKeyBytes.length);
		//System.arraycopy(decodedBytes, decodedBytes.length - encryptedKeys.length, encryptedKeys, 0, encryptedKeys.length);

		//create keys out of byte arrays that were extracted
		Key key_e = new SecretKeySpec(encryptionKeyBytes, ALGORITHM);
		Key key_i = new SecretKeySpec(integrityKeyBytes, ALGORITHM);

		System.out.println("KEY_e: " + Base64.toBase64String(key_e.getEncoded()));
		System.out.println("KEY_i: " + Base64.toBase64String(key_i.getEncoded()));

		//Checks if the tag matches
		if (Arrays.equals(hash, performHMAC(encryptedMessage, key_i))){
			//Decrypt the cipher text
			cipher.init(Cipher.DECRYPT_MODE, key_e, new IvParameterSpec(iv));
			byte[] plainText = cipher.doFinal(encryptedMessage);

			return new String(plainText);
		}
		else {
			System.out.println("HMAC DID NOT MATCH!");
			System.out.println("Original HMAC: " + Base64.toBase64String(hash));
			System.out.println("Calculated HMAC: " + Base64.toBase64String(performHMAC(encryptedMessage, key_i)));
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
	private byte[] performHMAC(byte[] cipherText, Key key_i) throws NoSuchAlgorithmException, InvalidKeyException{

		//create new mac and hash the cipherText
		Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
		hmac.init(key_i);

		return hmac.doFinal(cipherText);
	}
}
