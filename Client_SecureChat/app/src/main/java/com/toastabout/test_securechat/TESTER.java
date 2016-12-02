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

import Encryption.*;

import java.io.UnsupportedEncodingException;

public class TESTER extends AppCompatActivity {

	RSACipher rsaCipher;

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
		rsaCipher = new RSACipher();

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String testString = "Hello World!";

				//set plaintext
				tv1.setText(testString);

				//set ciphertext
				byte[] cipherText = rsaCipher.RSAEncrypt(testString);
				try {
					String cipherString = new String(cipherText, "UTF8");
					tv2.setText(cipherString);

					//set decrypted plaintext
					byte[] plainByte = rsaCipher.RSADecrypt(cipherString);
					String plainText = new String(plainByte, "UTF8");
					tv3.setText(plainText);
				}
				catch (UnsupportedEncodingException e) {
					Log.w("UnsupportedEncoding", e.getMessage());
				}
			}
		});









	}

}
