package Encryption;

/**
 * Created by Brett on 12/1/2016.
 */

import android.util.Log;

import java.security.NoSuchAlgorithmException;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESCipher {
	private Cipher cipher;
	private Base64 base64;
	private int passwordLength;
	private int saltLength;
	private int initializationVectorSeedLength;

	public AESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this(new Base64(), 16, 16, 16);
	}

		public AESCipher(Base64 base64, int passwordLength, int saltLength, int initializationVectorSeedLength)
				throws NoSuchAlgorithmException, NoSuchPaddingException {

			try {
				this.base64 = base64;
				this.passwordLength = passwordLength;
				this.saltLength = saltLength;
				this.initializationVectorSeedLength = initializationVectorSeedLength;
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				}
			catch (NoSuchAlgorithmException e) {
				Log.w("No Such Algorithm: ", e.getMessage());
			}
			catch (NoSuchPaddingException e) {
				Log.w("No Such Padding: ", e.getMessage());
			}

		}

	/**
	 *
	 */
	private SecretKey secretKey;

	/**
	 *
	 * @return
	 */
	public SecretKey getSecretKey() {
		return secretKey;
	}

	/**
	 *
	 * @param secretKey
	 * @return
	 */
	public String getEncodedSecretKey(SecretKey secretKey) {
		return base64.encodeToString(secretKey.getEncoded());
	}

	/**
	 *
	 * @param secretKey
	 * @return
	 */
	public SecretKey getDecodedSecretKey(String secretKey) {
		return new SecretKeySpec(base64.decode(secretKey), "AES");
	}

	/**
	 *
	 * @param rawText
	 * @param hashIterations
	 * @param keyLength
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String encrypt(String rawText, int hashIterations, KeyLength keyLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecureRandom secureRandom = new SecureRandom();

		byte[] seed = secureRandom.generateSeed(initializationVectorSeedLength);
		AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(seed);

		KeySpec keySpec = new PBEKeySpec(getRandomPassword(), secureRandom.generateSeed(saltLength), hashIterations, keyLength.getBits());
		secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		byte[] encryptedMessageBytes = cipher.doFinal(rawText.getBytes());

		byte[] bytesToEncode = new byte[seed.length + encryptedMessageBytes.length];
		System.arraycopy(seed, 0, bytesToEncode, 0, seed.length);
		System.arraycopy(encryptedMessageBytes, 0, bytesToEncode, seed.length, encryptedMessageBytes.length);

		return base64.encodeToString(bytesToEncode);
	}

	/**
	 *
	 * @param encryptedText
	 * @param secretKey
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(String encryptedText, SecretKey secretKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] bytesToDecode = base64.decode(encryptedText);

		byte[] emptySeed = new byte[initializationVectorSeedLength];
		System.arraycopy(bytesToDecode, 0, emptySeed, 0, initializationVectorSeedLength);

		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(emptySeed));

		int messageDecryptedBytesLength = bytesToDecode.length - initializationVectorSeedLength;
		byte[] messageDecryptedBytes = new byte[messageDecryptedBytesLength];
		System.arraycopy(bytesToDecode, initializationVectorSeedLength, messageDecryptedBytes, 0, messageDecryptedBytesLength);

		return new String(cipher.doFinal(messageDecryptedBytes));
	}

	/**
	 *
	 */
	public enum KeyLength {
		ONE_TWENTY_EIGHT(128),
		ONE_NINETY_TWO(192),
		TWO_FIFTY_SIX(256);

		private int bits;
		KeyLength(int bits) {
			this.bits = bits;
		}

		public int getBits() {
			return bits;
		}
	}

	/**
	 *
	 * @return
	 */
	protected char[] getRandomPassword() {

		char[] randomPassword = new char[passwordLength];

		Random random = new Random();
		for(int i = 0; i < passwordLength; i++) {
			randomPassword[i] = (char)(random.nextInt('~' - '!' + 1) + '!');
		}
		return randomPassword;
	}
}
