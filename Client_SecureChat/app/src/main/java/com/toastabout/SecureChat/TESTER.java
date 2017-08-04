package com.toastabout.SecureChat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.spongycastle.util.encoders.Base64;

import Encryption.*;

public class TESTER extends AppCompatActivity {

	private RSACipher rsaCipher;
	private AESCipher aesCipher;
	private Map<String, PublicKey> keys;
	ObjectMapper mapper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tester);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int WIDTH = displaymetrics.widthPixels;


		try {
			rsaCipher = new RSACipher(this);
		} catch (Exception e) {
			Log.d("rsaCipher: ", e.getMessage());
		}
		aesCipher = new AESCipher(this);

		String testString = "Hello World!";
		System.out.println("ORIGINAL STRING: " + testString + "\n\n");

		//TEST AES ENCRYPTION
//		try {
//			//set cipher text
//			String cipherText = aesCipher.encrypt(testString);
//			System.out.println("CIPHER TEXT: " + cipherText);
//
//			//set decrypted plaintext
//			String plainText = aesCipher.decrypt(cipherText);
//			System.out.println("PLAIN TEXT: " + plainText);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			Log.d("Exception: ", e.getMessage());
//		}

//		//TEST RSA ENCRYPTION
		try {
//
//			rsaCipher.addKeyToKeychain("Bob", rsaCipher.getPublicKey());
//			rsaCipher.addKeyToKeychain("Alice", rsaCipher.getPublicKey());
//			rsaCipher.addKeyToKeychain("Gary", rsaCipher.getPublicKey());
//			rsaCipher.addKeyToKeychain("Brett", rsaCipher.getPublicKey());
//			rsaCipher.addKeyToKeychain("Sam", rsaCipher.getPublicKey());
			keys = rsaCipher.getKeyChain();

			//display friends and their public keys from a file
			for (Map.Entry<String, PublicKey> entry : keys.entrySet()) {
				System.out.println("Friend: " + entry.getKey());
				System.out.println(entry.getKey() + "'s Public Key: " + Base64.toBase64String(entry.getValue().getEncoded()));
			}

			//encrypt sample text
			byte[] cipherBytes = rsaCipher.encrypt(testString.getBytes(), rsaCipher.getPublicKey());
			String cipherText = Base64.toBase64String(cipherBytes);
			System.out.println("CIPHER TEXT: " + cipherText + "\n\n");

			//decrypt sample text
			byte[] plainTextBytes = rsaCipher.decrypt(Base64.decode(cipherText));
			String plainText = new String(plainTextBytes);
			System.out.println("DECRYPTED TEXT : " + plainText + "\n\n");

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("Exception: ", e.getMessage());
		}
	}

	/**
	 * Get result from scanning barcode
	 * @param requestCode
	 * @param resultCode
	 * @param intent
     */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		TextView scanText = (TextView) findViewById(R.id.scanResults);
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {

			//display scan result text
			scanText.setText(scanResult.getContents());
			System.out.println(scanResult.getContents());
			//recreate Map from JSON String

			try {
				Map<String,String> friend = mapper.readValue(scanResult.getContents(), new TypeReference<HashMap<String, String>>() {
				});
				for (Map.Entry<String, String> entry : friend.entrySet()) {
					System.out.println("Friend: " + entry.getKey());
					System.out.println(entry.getKey() + "'s Public Key: " + entry.getValue());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				Log.d("IOException: ", e.getMessage());
			}
		}
		// else continue with any other code you need in the method

	}


}
