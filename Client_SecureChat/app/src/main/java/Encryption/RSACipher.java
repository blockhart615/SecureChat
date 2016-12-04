/**
 * use RSA for key transport
 * use AES for message encryption
 * use HMAC-SHA256 for message integrity, etc
 */

package Encryption;

import android.util.Log;

import org.spongycastle.util.encoders.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Brett on 12/1/2016.
 */

public class RSACipher {

	private final String ALGORITHM = "RSA";
	private final String ENCRYPTION_MODE = "ECB";
	private final String PADDING = "OAEPWithSHA-256AndMGF1Padding";
	private final int KEY_SIZE = 2048;


	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
	private File keyChainFile;
	private HashMap<String, PublicKey> keyChain;
	private Cipher cipher;
	private PublicKey publicKey;
	private PrivateKey privateKey;


	//SpongyCastle security provider.
	static {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	/**
	 * Default Constructor
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
     */
	public RSACipher() throws NoSuchAlgorithmException, NoSuchPaddingException{
		//PUBLIC KEY INITIALIZATIONS
		keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(KEY_SIZE);
		keyPair = keyGen.generateKeyPair();

		//get public and private keys
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

		cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" +PADDING);

		keyChainFile = new File("KeyChain");
		try {
			keyChain = getKeysFromFile(keyChainFile);
		}
		catch (Exception e) {
			Log.d("KeyChain Exception: ", e.getMessage());
		}
	}


	/**
	 * Encrypt using RSA public key
	 * @param plainText
	 * @return
	 */
	public String encrypt(String plainText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{

		// Get friend's public key from key chain
		// cipher.init(Cipher.ENCRYPT_MODE, keyChain.get(friend));
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return Base64.toBase64String(cipherText);
	}


	/**
	 * Decrypt usiong RSA private key
	 * @param cipherText
	 * @return
	 */
	public String decrypt(String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] cipherBytes = Base64.decode(cipherText);
		return new String(cipher.doFinal(cipherBytes));
	}


	/**
	 * adds a public key to the keychain and writes it to the file
	 * @param friend friend who's key you have
	 * @param publicKey public key of friend
	 * @throws IOException
     */
	public void addKeyToKeychain(String friend, PublicKey publicKey) throws IOException{
		keyChain.put(friend, publicKey);
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(keyChainFile));
		output.writeObject(keyChain);
		output.flush();
		output.close();
	}

	/**
	 * get keychain from file
	 * @param file file that holds keychain object
	 * @return keychain hashMap gotten from file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private HashMap<String,PublicKey> getKeysFromFile(File file) throws IOException, ClassNotFoundException{
		if (file.exists()) {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			Object obj = inputStream.readObject();
			if (obj instanceof HashMap<?, ?>) {
				return (HashMap<String, PublicKey>) obj;
			} else {
				return null;
			}
		}
		else {
			return null;
		}
	}

}
