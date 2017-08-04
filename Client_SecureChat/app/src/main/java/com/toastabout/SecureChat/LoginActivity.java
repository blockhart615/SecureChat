package com.toastabout.SecureChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import Classes.AccountManager;
import Classes.Constants;

public class LoginActivity extends AppCompatActivity {

	RequestHandler requester;
    AccountManager accountManager;
    String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Secure Chat");
		}

		//this should be gone soon......
		requester = new RequestHandler(LoginActivity.this);

        //if the file exists, read it and populate the text fields.
        readLoginFile();

		//Add listener to the login button
		Button loginBtn = (Button) findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			EditText username_field = (EditText) findViewById(R.id.username);
			EditText password_field = (EditText) findViewById(R.id.password);

			@Override
			public void onClick(View view) {
				String username = username_field.getText().toString().trim();
				String password = password_field.getText().toString().trim();

				//make sure fields aren't empty
				if (username.isEmpty())
					Toast.makeText(LoginActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				else if (password.isEmpty())
					Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
				else {
                    //TODO account manager should NOT handle switching between activities. That should
                    //TODO all be handled in the activities themselves.
                    //if no errors, send request to server
                    writeLoginInfoToFile(username);
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
			String username = extras.getString("username");
			EditText usernameField = (EditText) findViewById(R.id.username);
			usernameField.setText(username);
		}

	}//END onCreate

    /**
     * Reads login file which holds the username of the user who last logged in
     */
    private void readLoginFile() {
        try {
            FileInputStream inputStream = LoginActivity.this.openFileInput(Constants.LOGIN_FILENAME);
            // login file exists, so we want to read it's data
            // read the file and set the username/password fields
            Scanner scanner = new Scanner(inputStream);
            username = scanner.nextLine();  // read username
            scanner.close();

            //set edittext fields with contents from file
            EditText username_field = (EditText) findViewById(R.id.username);
            username_field.setText(username, TextView.BufferType.EDITABLE);
        }
        catch (IOException e) {
            //TODO Handle this exception
        }
    }



    /**
     * Writes the username of the last person who logged in to a file.
     * @param username the user's username
     */
    private void writeLoginInfoToFile(String username) {
        try {
            //write username and password to file
            FileOutputStream fos = LoginActivity.this.openFileOutput(Constants.LOGIN_FILENAME, Context.MODE_PRIVATE);
            PrintWriter out = new PrintWriter(fos);
            out.write(username);
            out.println();
            out.close();
        }
        catch (IOException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}//END LoginActivity.java
