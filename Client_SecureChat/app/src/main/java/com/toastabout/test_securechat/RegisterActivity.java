package com.toastabout.test_securechat;

import android.content.Intent;
import android.content.IntentSender;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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
				else if (password.getText().equals(confirmPassword.getText())) {
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
	public void registerUser(final String username, final String password, final String email) {

		Routes URL = new Routes();

		// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.POST, URL.getRegisterURL(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Display the response from the server
						try {
							JSONObject JSONresponse = new JSONObject(response);

							//IF the request is successful, do this:
							if (!JSONresponse.getBoolean("error")) {
								try {
									Intent intent = new Intent("com.toastabout.test_securechat.LoginActivity");
									intent.putExtra("username", JSONresponse.getString("username"));
									startActivity(intent);
								}
								catch (Exception e) {
									Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
								}
							}
							else {
								Toast.makeText(RegisterActivity.this, JSONresponse.getString("error_msg"), Toast.LENGTH_SHORT).show();
							}
						}
						catch (JSONException e) {
							Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}


					}//END ON_RESPPONSE
				},//END RESPONSE LISTENER
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(RegisterActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
					}
				})
		{
			@Override
			protected HashMap<String, String> getParams()
			{
				HashMap<String, String> params = new HashMap<>();
				params.put("username", username);
				params.put("password", password);
				params.put("email", email);
				return params;
			}
		};

		MySingleton.getInstance(this).addToRequestQueue(stringRequest);
	}//END registerUser



}//END CLASS
