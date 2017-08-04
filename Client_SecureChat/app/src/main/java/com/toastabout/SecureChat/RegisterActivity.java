package com.toastabout.SecureChat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

	private RequestHandler requester;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Create New User");
		}

		requester = new RequestHandler(this);

		//Click Listener for Register Button
		Button registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//get form elements as variables
				EditText username = (EditText) findViewById(R.id.username);
				EditText password = (EditText) findViewById(R.id.password);
				EditText confirmPassword = (EditText) findViewById(R.id.confirm_password);
				EditText email = (EditText) findViewById(R.id.email);

				String user = username.getText().toString();
				String passwordText = password.getText().toString();
				String confirmPassText = confirmPassword.getText().toString();
				String emailText = email.getText().toString();

				//if valid email, username isn't already taken, password and confirmation match
				if (user.isEmpty())
					Toast.makeText(RegisterActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				//if either password field is empty, error
				else if (passwordText.isEmpty() || confirmPassText.isEmpty())
					Toast.makeText(RegisterActivity.this, "Password field is empty", Toast.LENGTH_SHORT).show();
				//if email field is empty
				else if (emailText.isEmpty())
					Toast.makeText(RegisterActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
				//if passwords don't match
				else if (!passwordText.equals(confirmPassText))
					Toast.makeText(RegisterActivity.this, "Passwords did not match.", Toast.LENGTH_SHORT).show();
				//if all is good, send request to server
				else
					requester.registerUser(user, passwordText, emailText, RegisterActivity.this);
			}
		});


	}//END ON CREATE
}//END CLASS
