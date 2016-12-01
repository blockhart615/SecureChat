package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

	RequestHandler requester = new RequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Secure Chat");
		}


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
				else
                    //if no errors, send request to server
					requester.login(username, password, LoginActivity.this);
			}
		});

		//Add listener for register link
		TextView registerTxt = (TextView) findViewById(R.id.register_link);
		registerTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View view) {
				Intent intent = new Intent("com.toastabout.test_securechat.RegisterActivity");
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

}//END LoginActivity.java
