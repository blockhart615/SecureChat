package com.toastabout.test_securechat;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String jwt = extras.getString("jwt");
//			TextView tokenText = (TextView) findViewById(R.id.token);
//			tokenText.setText(jwt);
		}

		//Get messages when screen loads
		getMessages();


		ListView conversations = (ListView) findViewById(R.id.list);
		//swipe down to refresh, I.E. get messages from the server.
		final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getMessages();
				swipeRefreshLayout.setRefreshing(false);
			}
		});

	}


	/**
	 * getMessages performs the request to get the conversations from the server
	 */
	public void getMessages() {
		//TODO Implement GET messages
		Toast.makeText(this, "GETTING NEW MESSAGES!", Toast.LENGTH_SHORT).show();
	}

	public void enterConversation() {

	}
}
