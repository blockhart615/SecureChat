package com.toastabout.SecureChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Scanner;

import Classes.AccountManager;
import Classes.Constants;

public class LoginActivity extends AppCompatActivity {

    private AccountManager accountManager;
    private final File loginFile = new File(LoginActivity.this.getFilesDir().getPath() + Constants.LOGIN_FILENAME);
    private EditText usernameField = (EditText) findViewById(R.id.username);
    private EditText passwordField = (EditText) findViewById(R.id.password);
    String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Secure Chat");
		}

        //if the file exists, read it and populate the text fields.
        if (loginFile.exists()){
            readLoginFile(loginFile);
        }
        //clear password field in case it is still there
        passwordField.setText("");

		//Add listener to the login button
		Button loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				username = usernameField.getText().toString().trim();
				password = passwordField.getText().toString().trim();

				//make sure fields aren't empty
				if (username.isEmpty())
					Toast.makeText(LoginActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				else if (password.isEmpty())
					Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
				else {
                    //TODO account manager should NOT handle switching between activities. That should
                    //all be handled in the activities themselves.
                    //if no errors, send request to server
                    writeLoginInfoToFile(username, loginFile);
                    accountManager = new AccountManager();
                    accountManager.login(username, password, LoginActivity.this);
                }
			}
		});

		//Add listener for register link
		TextView registerTxt = (TextView) findViewById(R.id.register_link);
		registerTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				Intent intent = new Intent("com.toastabout.SecureChat.RegisterActivity");
				startActivity(intent);
			}
		});

        //if user just registered, username is filled in for them to log in.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
			usernameField.setText(username);
		}

	}//END onCreate

    /**
     * Reads login file which holds the username of the user who last logged in
     * @param loginFile the login file that contains the last used username
     */
    private void readLoginFile(File loginFile) {
		if (loginFile.exists()) {
			try {
                //get object stream to read username from file
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(loginFile));
				Object obj = inputStream.readObject();
				if (obj instanceof String) {
                    //auto fill login name with contents from file
                    usernameField.setText((String) obj, TextView.BufferType.EDITABLE);
				}
				inputStream.close();
			}
			catch (Exception e) {
                Log.d("Error reading login: ", e.getMessage());
			}
		}
    }



    /**
     * Writes the username of the last person who logged in to a file.
     * @param username the user's username
     * @param loginFile the file where the username will be written
     */
    private void writeLoginInfoToFile(String username, File loginFile) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(loginFile);
            ObjectOutputStream output = new ObjectOutputStream(fileOutput);
            output.writeObject(username);
            output.flush();
            output.close();
        }
        catch (IOException e) {
            Log.d("Error writing login: ", e.getMessage());
        }
    }

}//END LoginActivity.java
