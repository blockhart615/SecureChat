package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.toastabout.test_securechat.Routes;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


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
				if (username.isEmpty()) {
					Toast.makeText(LoginActivity.this, "Username field is empty", Toast.LENGTH_SHORT).show();
				}
				else if (password.isEmpty()) {
					Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
				}
				else {
					login(username, password);
				}
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


	}


	public void login(String username, String password) {

		Routes URL = new Routes();

		HashMap<String, String> POSTparams = new HashMap<>();
		POSTparams.put("username", username);
		POSTparams.put("password", password);
		JSONObject requestObject = new JSONObject(POSTparams);


		JsonObjectRequest jsObjRequest = new JsonObjectRequest
				(Request.Method.POST, URL.getLoginURL(), requestObject, new Response.Listener<JSONObject>() {
					String jwt = "";

					@Override
					public void onResponse(JSONObject response) {
						jwt = response.toString();
						Toast.makeText(LoginActivity.this, jwt, Toast.LENGTH_SHORT).show();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
					}
				});

		MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);





//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		StrictMode.setThreadPolicy(policy);
//
////		try {
////			URL url = new URL(URL.getLoginURL());
////			String urlParams = "username="+username+"&password="+password;
////
////			//set up HTTP Connection
////			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
////			httpURLConnection.setDoOutput(true);
////			OutputStream os = httpURLConnection.getOutputStream();
////			os.write(urlParams.getBytes());
////			os.flush();
////			os.close();
////
////			//get input from server and store into variable.
////			//if credentials are correct, will return a jwt
////			InputStream is = httpURLConnection.getInputStream();
////			while((tmp = is.read()) != -1){
////				jwt += (char)tmp;
////			}
////
////			//close input stream and disconnect connection
////			is.close();
////			httpURLConnection.disconnect();
////
////
////			//if login is successful, send user to Inbox
////			Intent intent = new Intent("com.toastabout.test_securechat.InboxActivity");
////			intent.putExtra("jwt", jwt);
////			startActivity(intent);
////
////		}
////		catch (Exception e) {
////			e.printStackTrace();
////		}

	}

}
