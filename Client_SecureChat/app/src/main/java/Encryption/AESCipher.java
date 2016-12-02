package Encryption;

/**
 * use AES for message encryption
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
	private SecretKey secretKey;
	private int passwordLength;
	private int saltLength;
	private int IVSeedLength;

	public AESCipher() throws NoSuchAlgorithmException, NoSuchPaddingException {
		this(new Base64(), 16, 16, 16);
	}

	public AESCipher(Base64 base64, int passwordLength, int saltLength, int IVSeedLength)
			throws NoSuchAlgorithmException, NoSuchPaddingException {

			try {
				this.base64 = base64;
				this.passwordLength = passwordLength;
				this.saltLength = saltLength;
				this.IVSeedLength = IVSeedLength;
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
	 * @return the Secret Key
	 */
	public SecretKey getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secret key
	 * @return base64 encoded secret key
	 */
	public String getEncodedSecretKey(SecretKey secretKey) {
		return base64.encodeToString(secretKey.getEncoded());
	}

	/**
	 * Decodes the secret key
	 * @param secretKey the encoded secret key
	 * @return the decoded secret key
	 */
	public SecretKey getDecodedSecretKey(String secretKey) {
		return new SecretKeySpec(base64.decode(secretKey), "AES");
	}

	/**
	 * Encrypts a string
	 * @param plainText the plain text as a String
	 * @param hashIterations the number of iterations of the hash function
	 * @param keyLength the length of the key
	 * @return encrypted string
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String encrypt(String plainText, int hashIterations, KeyLength keyLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecureRandom secureRandom = new SecureRandom();

		byte[] seed = secureRandom.generateSeed(IVSeedLength);
		AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(seed);

		KeySpec keySpec = new PBEKeySpec(getRandomPassword(), secureRandom.generateSeed(saltLength), hashIterations, keyLength.getBits());
		secretKey = new SecretKeySpec(secretKeyFactory.generateSecret(keySpec).getEncoded(), "AES");

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, algorithmParameterSpec);
		byte[] encryptedMessageBytes = cipher.doFinal(plainText.getBytes());

		byte[] bytesToEncode = new byte[seed.length + encryptedMessageBytes.length];
		System.arraycopy(seed, 0, bytesToEncode, 0, seed.length);
		System.arraycopy(encryptedMessageBytes, 0, bytesToEncode, seed.length, encryptedMessageBytes.length);

		return base64.encodeToString(bytesToEncode);
	}

	/**
	 * Decrypts ciphertext
	 * @param encryptedText Encrypted text as a String
	 * @param secretKey The secret key needed to decrypt the cipher text
	 * @return decrypted text
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(String encryptedText, SecretKey secretKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		byte[] bytesToDecode = base64.decode(encryptedText);

		byte[] emptySeed = new byte[IVSeedLength];
		System.arraycopy(bytesToDecode, 0, emptySeed, 0, IVSeedLength);

		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(emptySeed));

		//get length of message by subtracting the IV length
		int messageDecryptedBytesLength = bytesToDecode.length - IVSeedLength;
		byte[] messageDecryptedBytes = new byte[messageDecryptedBytesLength];
		System.arraycopy(bytesToDecode, IVSeedLength, messageDecryptedBytes, 0, messageDecryptedBytesLength);

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
	 * @return the random password
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
