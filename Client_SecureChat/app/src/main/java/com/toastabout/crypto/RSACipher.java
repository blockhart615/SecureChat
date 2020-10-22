/**
 * use RSA for key transport
 * use AES for message encryption
 * use HMAC-SHA256 for message integrity, etc
 */

package Crypto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;

import org.spongycastle.util.encoders.Base64;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Classes.Constants;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


/**
 * Created by Brett on 12/1/2016.
 */

public class RSACipher {

	private final String ALGORITHM = "RSA";
	private final String ENCRYPTION_MODE = "NONE";
	private final String PADDING = "OAEPWithSHA-256AndMGF1Padding";
	private final int KEY_SIZE = 2048;
//	private int WIDTH;


	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
	private File keyChainFile, myKeysFile;
	private Map<String, PublicKey> keyChain;
	private Cipher cipher;
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private ObjectMapper mapper;


	//SpongyCastle security provider.
	static {
		Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
	}


	public RSACipher(Context context) throws NoSuchAlgorithmException, NoSuchPaddingException{

		cipher = Cipher.getInstance(ALGORITHM + "/" + ENCRYPTION_MODE + "/" +PADDING);
		//generate key pair and write to file
		keyGen = KeyPairGenerator.getInstance(ALGORITHM);
		keyGen.initialize(KEY_SIZE);
		keyPair = keyGen.generateKeyPair();
		//get public and private keys
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

		//file paths
		keyChainFile = new File(context.getFilesDir().getPath() + Constants.KEYCHAIN_FILENAME); //file with friends' public keys
		myKeysFile = new File(context.getFilesDir().getPath() + Constants.MYKEYS_FILENAME); //file with public/private key

		//if my key File exsits, get keypair from file
		if (myKeysFile.exists()) {
			try {
				keyPair = getKeyPairFromFile(myKeysFile);
				if (keyPair != null) {
					publicKey = keyPair.getPublic();
					privateKey = keyPair.getPrivate();
					System.out.println(Base64.toBase64String(publicKey.getEncoded()));
				}
			}
			catch (Exception e) {
				Log.d("MyKeys Exception: ", e.getMessage());
			}
		}
		//else generate a new keypair and store it in file.
		else {
			try {
				writeMyKeysToFile(keyPair);
			}
			catch (IOException e){
				Log.w("Error Storing KeyPair: ", e.getMessage());
			}
		}

		//if keyChainFile exists, then get the map from the file
		if(keyChainFile.exists()) {
			try {
				keyChain = getKeysFromFile(keyChainFile);
			} catch (Exception e) {
				Log.d("KeyChain Exception: ", e.getMessage());
			}
		}
		//if it doesn't exist yet, create a new one
		else {
			keyChain = new HashMap<>();
		}

	}


	/**
	 * Encrypt using RSA public key
	 * @param plainText
	 * @return
	 */
	public byte[] encrypt(byte[] plainText, PublicKey publicKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		// Get friend's public key from key chain
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(plainText);
	}


	/**
	 * Decrypt usiong RSA private key
	 * @param cipherText
	 * @return
	 */
	public byte[] decrypt(byte[] cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(cipherText);
	}


	/**
	 * adds a public key to the keychain and writes it to the file
	 * @param friend friend who's key you have
	 * @param publicKey public key of friend
	 * @throws IOException
     */


	public void addKeyToKeychain(String friend, PublicKey publicKey) throws IOException{
		keyChain.put(friend, publicKey);
		FileOutputStream fileOutput = new FileOutputStream(keyChainFile);
		ObjectOutputStream output = new ObjectOutputStream(fileOutput);
		output.writeObject(keyChain);
		output.flush();
		output.close();
	}

	public void writeMyKeysToFile(KeyPair myKeyPair) throws IOException {
		FileOutputStream fileOutput = new FileOutputStream(myKeysFile);
		ObjectOutputStream output = new ObjectOutputStream(fileOutput);
		output.writeObject(myKeyPair);
		output.flush();
		output.close();
	}


	/**
	 * get the public key
	 * @return public key
     */
	public PublicKey getPublicKey() {
		return publicKey;
	}


	/**
	 * get keychain from file
	 * @param file file that holds keychain object
	 * @return keychain hashMap gotten from file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Map<String,PublicKey> getKeysFromFile(File file) throws IOException, ClassNotFoundException{
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

	/**
	 * get My public and private key from
	 * @param file file that holds keychain object
	 * @return keychain hashMap gotten from file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private KeyPair getKeyPairFromFile(File file) throws IOException, ClassNotFoundException{
		if (file.exists()) {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			Object obj = inputStream.readObject();
			if (obj instanceof KeyPair) {
				return (KeyPair) obj;
			} else {
				return null;
			}
		}
		else {
			return null;
		}
	}


	/**
	 * get key chain map
	 * @return key chain
     */
	public Map<String,PublicKey> getKeyChain() {
		return keyChain;
	}


	/**
	 * get public key from friend and store in file
	 */
	public PublicKey receivePublicKey(Activity activity) {
		//open scanner
		//get JSON String
		//convert to hashmap<String, PublicKey>
		//put in keychain

		Map<String,String> friend = new HashMap<>();
		mapper = new ObjectMapper();

//		friend.put("Bob", Base64.toBase64String(rsaCipher.getPublicKey().getEncoded()));
//
//		try {
//			String jsonFromMap = mapper.writeValueAsString(friend);
//			rsaCipher.sendPublicKey(imageView, jsonFromMap);
//		}
//		catch (JsonProcessingException e) {
//			Log.d("JSON Processing error: ", e.getMessage());
//		}

		//Start scanning when button is pressed
		final IntentIntegrator integrator = new IntentIntegrator(activity);
		integrator.initiateScan();

		return null;
	}


	/**
	 * generate QR code for friend to get code from
	 * @param imageView
     */
	public void sendPublicKey(ImageView imageView, String username, String myPublicKey) {

		//create map of frien's data
		HashMap<String, String> myData = new HashMap<>();
		myData.put(username, myPublicKey);
		mapper = new ObjectMapper();
		try {
			//convert map into JSON string
			String jsonFromMap = mapper.writeValueAsString(myData);
			Bitmap bitmap = encodeAsBitmap(jsonFromMap);
			imageView.setImageBitmap(bitmap);
		}
		catch (JsonProcessingException e) {
			Log.d("JSON Processing error: ", e.getMessage());
		}
		catch (WriterException e) {
			e.printStackTrace();
		}
	}


	private Bitmap encodeAsBitmap(String JSONmapEntry) throws WriterException {

		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(JSONmapEntry,
					BarcodeFormat.QR_CODE, 700, 700, null);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			Log.d("Unsupported Format: ", iae.getMessage());
			return null;
		}
		int w = result.getWidth();
		int h = result.getHeight();

		int[] pixels = new int[w * h];
		for (int y = 0; y < h; y++) {
			int offset = y * w;
			for (int x = 0; x < w; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, 700, 0, 0, w, h);
		return bitmap;
	}


//	/**
//	 * Get result from scanning barcode
//	 * @param requestCode
//	 * @param resultCode
//	 * @param intent
//	 */
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//
//		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//		if (scanResult != null) {
//
//			//recreate Map from JSON String
//			try {
//				Map<String,String> friend = mapper.readValue(scanResult.getContents(), new TypeReference<HashMap<String, String>>() {
//				});
//				for (Map.Entry<String, String> entry : friend.entrySet()) {
//					System.out.println("Friend: " + entry.getKey());
//					System.out.println(entry.getKey() + "'s Public Key: " + entry.getValue());
//					org.spongycastle.asn1.pkcs.RSAPublicKey pkcs1PublicKey = org.spongycastle.asn1.pkcs.RSAPublicKey.getInstance(entry.getValue().getBytes());
//					BigInteger modulus = pkcs1PublicKey.getModulus();
//					BigInteger publicExponent = pkcs1PublicKey.getPublicExponent();
//					RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
//					try {
//						KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
//						PublicKey generatedPublic = kf.generatePublic(keySpec);
//						keyChain.put(entry.getKey(), generatedPublic);
//					}
//					catch (Exception e) {
//						Log.d("KeyFactory: ", e.getMessage());
//					}
//				}
//
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//				Log.d("IOException: ", e.getMessage());
//			}
//		}
//		// else continue with any other code you need in the method
//
//	}

}
