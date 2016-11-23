package com.toastabout.test_securechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
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

	private static JSONObject JSONresponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//ONLY CALL THIS IF RECEIVED JWT
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//gets token and username from previous activity
			String jwt = extras.getString("jwt");
			final String username = extras.getString("username");


			final ListView lv = (ListView) findViewById(R.id.list);
			final ArrayList<String> conversations = new ArrayList<>();


			//Get messages when screen loads
			getConversations(username, lv, conversations);

			//swipe down to refresh, I.E. get messages from the server.
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					getConversations(username, lv, conversations);
					swipeRefreshLayout.setRefreshing(false);
				}
			});

			//Add on click listener to list view to get desired conversation
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					String friend = (String) lv.getItemAtPosition(i);

					Intent intent = new Intent("com.toastabout.test_securechat.ChatActivity");
					try {
						String messages = JSONresponse.getJSONObject("conversations").getString(friend);
						intent.putExtra("messages", messages);
					}
					catch (JSONException e) {
						Toast.makeText(InboxActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}

					intent.putExtra("friend", friend);
					startActivity(intent);
				}
			});

		}

	}


	/**
	 * getMessages performs the request to get the conversations from the server
	 */
	public void getConversations(String username, final ListView lv, final ArrayList<String> conversations) {

		Routes URL = new Routes();

		// This is the array adapter, it takes the context of the activity as a first parameter,
		// the type of list view as a second parameter and your array as a third parameter.
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, conversations);


		//STRING REQUEST TO GET MESSAGES FROM SERVER
		StringRequest stringRequest = new StringRequest(Request.Method.GET, URL.getGetMessagesURL(username),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						// GET the response from the server
						try {
							JSONresponse = new JSONObject(response);
							JSONObject JSONconvos = JSONresponse.getJSONObject("conversations");
							Iterator<String> iter = JSONconvos.keys();
							while (iter.hasNext()) {
								String next = iter.next();
								if (!conversations.contains(next))
									conversations.add(next);
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

}
