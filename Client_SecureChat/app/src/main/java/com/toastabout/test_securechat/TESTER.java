package com.toastabout.test_securechat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.spongycastle.util.encoders.Base64;

import Encryption.*;

import java.io.UnsupportedEncodingException;

public class TESTER extends AppCompatActivity {

	RSACipher rsaCipher;
	AESCipher aesCipher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tester);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final TextView tv1 = (TextView) findViewById(R.id.tv1);
		final TextView tv2 = (TextView) findViewById(R.id.tv2);
		final TextView tv3 = (TextView) findViewById(R.id.tv3);
		final Button button = (Button) findViewById(R.id.button);
		try {
			rsaCipher = new RSACipher();
		}
		catch (Exception e) {
			Log.d("rsaCipher: ", e.getMessage());
		}
		aesCipher = new AESCipher();



		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String testString = "Hello World!";

				//set plaintext
				tv1.setText(testString);

				//TEST AES ENCRYPTION
//				try {
//					//set cipher text
//					String cipherText = aesCipher.encrypt(testString);
//
//					tv2.setText(cipherText);
//
//					//set decrypted plaintext
//					String plainText = aesCipher.decrypt(cipherText);
//					tv3.setText(plainText);
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//					Log.d("Exception: ", e.getMessage());
//				}

				//TEST RSA ENCRYPTION
				try {
					//encrypt sample text
					String cipherText = rsaCipher.encrypt(testString);
					tv2.setText(cipherText);

					//decrypt sample text
					String plainText = rsaCipher.decrypt(cipherText);
					tv3.setText(plainText);

				}
				catch (Exception e) {
					e.printStackTrace();
					Log.d("Exception: ", e.getMessage());
				}


			}
		});









	}

}
