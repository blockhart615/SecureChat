package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
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


		//ONLY CALL THIS IF RECEIVED JWT
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			//gets token and username from previous activity
			String jwt = extras.getString("jwt");
			final String username = extras.getString("username");

			//swipe down to refresh, I.E. get messages from the server.
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					getMessages(username);
					swipeRefreshLayout.setRefreshing(false);
				}
			});


			//Get messages when screen loads
			getMessages(username);
		}




	}


	/**
	 * getMessages performs the request to get the conversations from the server
	 */
	public void getMessages(String username) {
		//TODO Implement GET messages
		Toast.makeText(this, "GETTING NEW MESSAGES!", Toast.LENGTH_SHORT).show();

		final ListView lv = (ListView) findViewById(R.id.list);

		// Instanciating an array list (you don't need to do this,
		// you already have yours).
		final ArrayList<String> conversations = new ArrayList<>();

		// This is the array adapter, it takes the context of the activity as a
		// first parameter, the type of list view as a second parameter and your
		// array as a third parameter.
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				conversations);



		Routes URL = new Routes();
		
		//STRING REQUEST TO GET MESSAGES FROM SERVER
		StringRequest stringRequest = new StringRequest(Request.Method.GET, URL.getGetMessagesURL(username),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						// GET the response from the server
						try {
							JSONObject JSONresponse = new JSONObject(response);
							JSONObject JSONconvos = JSONresponse.getJSONObject("conversations");
							Iterator<String> iter = JSONconvos.keys();
							while (iter.hasNext()) {
								conversations.add(iter.next());
							}
							lv.setAdapter(arrayAdapter);

							//IF NO ERRORS, DO THIS!
							if (!JSONresponse.getBoolean("error")) {
								//TODO if no errors, set conversations to listview
							}
							//DISPLAY ERROR MESSAGE
							else {
								Toast.makeText(InboxActivity.this, JSONresponse.getString("error_msg"), Toast.LENGTH_SHORT).show();
							}
						}
						catch (JSONException e) {
							Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}


					}//END ON_RESPONSE
				},//END RESPONSE LISTENER
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(InboxActivity.this, "That didn't work!", Toast.LENGTH_SHORT).show();
					}
				})
		{
			@Override
			protected HashMap<String, String> getParams()
			{
				HashMap<String, String> params = new HashMap<>();
//				params.put("username", username);
//				params.put("password", password);
				return params;
			}
		};
		//add response to queue to be sent to server
		MySingleton.getInstance(this).addToRequestQueue(stringRequest);

	}

	/**
	 *
	 */
	public void getConversations() {

	}

	/**
	 *
	 */
	public void enterConversation() {

	}
}
