package com.toastabout.test_securechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

	private RequestHandler requester = new RequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Create New User");
		}

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

				//if valid email, username isn't already taken, password and confirmation match
				if (username.getText().toString().isEmpty())
					Toast.makeText(RegisterActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				else if (password.getText().toString().isEmpty())
					Toast.makeText(RegisterActivity.this, "Password field is empty", Toast.LENGTH_SHORT).show();
				else if (email.getText().toString().isEmpty())
					Toast.makeText(RegisterActivity.this, "Email field is empty", Toast.LENGTH_SHORT).show();
				else if (password.getText().equals(confirmPassword.getText()))
					Toast.makeText(RegisterActivity.this, "Passwords did not match.", Toast.LENGTH_SHORT).show();
				//if all is good, send request to server
				else
					requester.registerUser(username.getText().toString(), password.getText().toString(), email.getText().toString(), RegisterActivity.this);
			}
		});


	}//END ON CREATE
}//END CLASS
