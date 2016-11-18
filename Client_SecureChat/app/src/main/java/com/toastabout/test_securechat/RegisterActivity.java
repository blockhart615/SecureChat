package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		Button registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				EditText username = (EditText) findViewById(R.id.username);
				EditText password = (EditText) findViewById(R.id.password);
				EditText confirmPassword = (EditText) findViewById(R.id.confirm_password);
				EditText email = (EditText) findViewById(R.id.email);

				//if valid email, username isn't already taken, password and confirmation match
				if (username.getText().toString().isEmpty()) {
					Toast.makeText(RegisterActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				}
				else if (password.getText().toString().isEmpty()) {
					Toast.makeText(RegisterActivity.this, "Password field is empty", Toast.LENGTH_SHORT).show();
				}
				else if (email.getText().toString().isEmpty()) {
					Toast.makeText(RegisterActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
				}
				else if (password.getText() != confirmPassword.getText()) {
					Toast.makeText(RegisterActivity.this, "Passwords did not match.", Toast.LENGTH_SHORT).show();
				}
				else {
					registerUser(username.getText().toString(), password.getText().toString(), email.getText().toString());
				}


			}
		});
	}//END ON CREATE


	/**
	 * Registers a user in the database
	 * @param username desired username of the user
	 * @param password desire password of the user
	 * @param email User's email address
	 */
	public void registerUser(String username, String password, String email) {
		//TODO Add Register User Code.
		int tmp;
		String response = "";
		Routes URL = new Routes();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			URL url = new URL(URL.getRegisterURL());
			String urlParams = "username=" + username + "&password=" + password + "&email=" + email;

			//set up HTTP Connection
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			OutputStream os = httpURLConnection.getOutputStream();
			os.write(urlParams.getBytes());
			os.flush();
			os.close();

			//get input from server and store into variable.
			//if credentials are correct, will return a jwt
			InputStream is = httpURLConnection.getInputStream();
			while((tmp = is.read()) != -1){
				response += (char)tmp;
			}

			//close input stream and disconnect connection
			is.close();
			httpURLConnection.disconnect();

			//output response for debugging purposes
			TextView token = (TextView) findViewById(R.id.token);
			token.setText(response);

			//if login is successful, send user to Inbox
			Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent("com.toastabout.test_securechat.LoginActivity");
			startActivity(intent);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}//END registerUser



}//END CLASS
