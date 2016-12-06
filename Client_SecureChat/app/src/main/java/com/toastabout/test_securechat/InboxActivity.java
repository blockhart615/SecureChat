package com.toastabout.test_securechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.spongycastle.util.encoders.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;

import Encryption.RSACipher;

public class InboxActivity extends AppCompatActivity {

	private RequestHandler requester;
	private String jwt, username, friend;
	private ListView lv;
	private ArrayList<String> conversations;
	private Intent ChatIntent;
	private Intent ExchangeIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("Inbox");
		}

		requester = new RequestHandler(this);
		ChatIntent = new Intent("com.toastabout.test_securechat.ChatActivity");
		ExchangeIntent = new Intent("com.toastabout.test_securechat.KeyExchange");

		//FAB to create new conversation with a user
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
				builder.setTitle("Start new Conversation with: ");

				final EditText newChatInput = new EditText(InboxActivity.this);
				builder.setView(newChatInput);

				//button to start a new conversation
				builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!newChatInput.getText().toString().equals("")) {
							friend = newChatInput.getText().toString();
							ChatIntent.putExtra("username", username);
							ChatIntent.putExtra("friend", friend);
							ChatIntent.putExtra("jwt", jwt);
							startActivity(ChatIntent);
						}
						else {
							Toast.makeText(InboxActivity.this, "You need to enter a username.", Toast.LENGTH_SHORT).show();
						}
					}
				});
				//button sends you to activity that exchanges keys
				builder.setNegativeButton("Exchange Keys", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ExchangeIntent.putExtra("username", username);
						startActivity(ExchangeIntent);
					}
				});

				builder.show();
			}
		});

		//ONLY CALL THIS IF RECEIVED JWT
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//gets token and username from previous activity
			jwt = extras.getString("jwt");
			username = extras.getString("username");
			lv = (ListView) findViewById(R.id.list);
			conversations = new ArrayList<>();

			//Get messages when screen loads
			requester.getConversations(username, lv, conversations, InboxActivity.this);


			//swipe down to refresh, I.E. get messages from the server.
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
			swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {

					requester.getConversations(username, lv, conversations, InboxActivity.this);
                    Log.w("GetConversations Object", requester.getGETresponse().toString());

					swipeRefreshLayout.setRefreshing(false);
				}
			});


			//Add on click listener to list view to get desired conversation
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

					//get name of friend from the list
					friend = (String) lv.getItemAtPosition(i);

					//fill intent Extras, and start new activity
					ChatIntent.putExtra("username", username);
					ChatIntent.putExtra("friend", friend);
					ChatIntent.putExtra("jwt", jwt);
					startActivity(ChatIntent);
				}
			});
		}
	}//END onCreate

}//END InboxActivity
